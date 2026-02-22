package com.example.templeinfo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.regex.Pattern;

@RestController
public class TempleController {

    private final WebPageFetcher webPageFetcher;
    private final TempleService templeService;

    @Autowired
    public TempleController(WebPageFetcher webPageFetcher, TempleService templeService) {
        this.webPageFetcher = webPageFetcher;
        this.templeService = templeService;
    }

    @PostMapping("/temple-info")
    public ResponseEntity<Temple> getTempleInfo(@RequestBody TempleUrlRequest request) throws IOException {
        try {
            Long id = request.getId();
            String url = "https://temple.dinamalar.com/en/new_en.php?id=" + id;
            Document doc = webPageFetcher.fetch(url);

            // --- Name Extraction ---
            String name = extractWithSelector(doc, "span#HyperLink2");
            if (name == null) {
                name = extractWithSelector(doc, "span.subhead");
            }
            // --- End Name Extraction ---

            String moolavar = extractField(doc, "Moolavar");
            String urchavar = extractField(doc, "Urchavar");
            String ammanThayar = extractField(doc, "Amman / Thayar");
            String thalaVirutcham = extractField(doc, "Thala Virutcham");
            String theertham = extractField(doc, "Theertham");
            String agamamPooja = extractField(doc, "Agamam / Pooja");
            String oldYear = extractField(doc, "Old year");
            String historicalName = extractField(doc, "Historical Name");
            String city = extractField(doc, "City");
            String district = extractField(doc, "District");
            String state = extractField(doc, "State");

            String singers = extractField(doc, "Singers");
            String festival = extractField(doc, "Festival");
            String generalInformation = extractField(doc, "General Information");
            String address = extractField(doc, "Address");
            String phone = extractField(doc, "Phone");
            String openingTime = extractField(doc, "Opening Time");
            String speciality = extractField(doc, "Speciality"); // Simplified label
            String prayers = extractField(doc, "Prayers");
            String thanksGiving = extractField(doc, "Thanks giving");
            String greatness = extractField(doc, "Greatness"); // Simplified label
            String history = extractField(doc, "History"); // Simplified label
            String features = extractField(doc, "Features"); // Simplified label

            Double hfLat = null;
            Double hfLan = null;

            try {
                String latStr = extractValueBySelector(doc, "input#hfLat", "value");
                if (latStr != null && !latStr.isEmpty()) {
                    hfLat = Double.valueOf(latStr);
                }
            } catch (NumberFormatException e) {
                System.err.println("Warning: Could not parse hfLat as Double: " + e.getMessage());
            }

            try {
                String lanStr = extractValueBySelector(doc, "input#hfLan", "value");
                if (lanStr != null && !lanStr.isEmpty()) {
                    hfLan = Double.valueOf(lanStr);
                }
            } catch (NumberFormatException e) {
                System.err.println("Warning: Could not parse hfLan as Double: " + e.getMessage());
            }
            
            Temple temple = new Temple(
                id,
                name,
                moolavar,
                urchavar,
                ammanThayar,
                thalaVirutcham,
                theertham,
                agamamPooja,
                oldYear,
                historicalName,
                city,
                district,
                state,
                singers,
                festival,
                generalInformation,
                address,
                phone,
                openingTime,
                speciality,
                prayers,
                thanksGiving,
                greatness,
                history,
                features,
                hfLat,
                hfLan
            );

            System.out.println("Temple info extracted: " + temple);

            try {
                Temple savedTemple = templeService.saveOrUpdateTemple(temple);
                return new ResponseEntity<>(savedTemple, HttpStatus.OK);
            } catch (DataAccessException e) {
                System.err.println("Error saving/updating temple in database: " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e) {
            System.err.println("Error during extraction: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY); // Or another appropriate error for web fetching
        }
    }

    /**
     * Extracts an attribute value from an element found by a CSS selector.
     */
    private String extractValueBySelector(Document doc, String selector, String attribute) {
        Element element = doc.selectFirst(selector);
        if (element != null) {
            return element.attr(attribute).trim();
        }
        return null;
    }

    /**
     * Extracts text content based on a direct CSS selector.
     * Useful for elements like h1, h2, specific divs etc.
     */
    private String extractWithSelector(Document doc, String selector) {
        Elements elements = doc.select(selector);
        if (!elements.isEmpty()) {
            return elements.first().text().trim();
        }
        return null;
    }

    /**
     * Unified field extraction. Tries multiple strategies to find the value for a given label.
     */
    private String extractField(Document doc, String label) {
        // Strategy 1: Table row pattern <td>Label</td><td>:</td><td>Value</td>
        String value = extractValueFromTableRow(doc, label);
        if (value != null) return value;

        // Strategy 2: Section pattern (Label in span.topic/span.subhead, content in newsdetails below)
        value = extractSectionContent(doc, label);
        if (value != null) return value;

        return null;
    }

    private String extractSectionContent(Document doc, String label) {
        Element labelElement = findElementByText(doc, "span.topic, span.subhead, td.style8, td.style2", label);
        if (labelElement == null) return null;

        // Try same row first (in case it's a table row but not matching style8)
        Element parentRow = labelElement.closest("tr");
        if (parentRow != null) {
            // Check for value in the same row
            Elements sameRowValue = parentRow.select("td.style5, td.newsdetails");
            for (Element v : sameRowValue) {
                if (!v.text().trim().isEmpty() && !v.text().contains(label)) {
                    return v.text().trim();
                }
            }

            // Try next rows
            Element nextRow = parentRow.nextElementSibling();
            int rowLimit = 15;
            while (nextRow != null && rowLimit-- > 0) {
                Element newsDetail = nextRow.selectFirst("td.newsdetails, td.style5");
                if (newsDetail != null && !newsDetail.text().trim().isEmpty()) {
                    String text = newsDetail.text().trim();
                    if (!text.equals(":") && !text.equals("-")) {
                        return text;
                    }
                }
                // Stop if we hit another significant label
                if (!nextRow.select("span.topic, span.subhead").isEmpty()) {
                    if (!nextRow.text().contains(label)) {
                        break;
                    }
                }
                nextRow = nextRow.nextElementSibling();
            }
        }
        return null;
    }

    private Element findElementByText(Document doc, String selector, String text) {
        Elements elements = doc.select(selector);
        for (Element el : elements) {
            String elText = el.text().toLowerCase();
            String searchLabel = text.toLowerCase();
            if (elText.contains(searchLabel)) {
                return el;
            }
        }
        return null;
    }

    private String extractValueFromTableRow(Document doc, String label) {
        Element labelCell = findElementByText(doc, "td.style8, td.style2, td.subhead", label);
        if (labelCell == null) return null;

        Element colonCell = labelCell.nextElementSibling();
        if (colonCell != null && colonCell.text().trim().contains(":")) {
            Element valueCell = colonCell.nextElementSibling();
            if (valueCell != null && !valueCell.text().trim().isEmpty()) {
                return valueCell.text().trim();
            }
        }
        
        // Fallback for cases where colon is in the same cell or missing
        String cellText = labelCell.text().trim();
        if (cellText.contains(":") && cellText.indexOf(":") < cellText.length() - 1) {
            return cellText.substring(cellText.indexOf(":") + 1).trim();
        }

        return null;
    }
}
