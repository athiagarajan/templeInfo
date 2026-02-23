package com.example.templeinfo;

import org.jsoup.Jsoup;
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
import java.util.HashMap;
import java.util.Map;
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

            String location = extractField(doc, "Location");
            String nearByAirport = extractField(doc, "Near By Airport");
            String nearByRailwayStation = extractField(doc, "Near By Railway Station");
            String accommodation = extractField(doc, "Accomodation");
            
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
                hfLan,
                location,
                nearByAirport,
                nearByRailwayStation,
                accommodation
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

    @PostMapping("/temple-info/batch")
    public ResponseEntity<Map<String, String>> batchExtractTempleInfo(@RequestBody TempleIdRangeRequest request) {
        Map<String, String> results = new HashMap<>();
        Long startId = request.getStartId();
        Long endId = request.getEndId();

        if (startId == null || endId == null || startId > endId) {
            results.put("error", "Invalid ID range provided. Please ensure startId and endId are valid and startId <= endId.");
            return new ResponseEntity<>(results, HttpStatus.BAD_REQUEST);
        }

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (long id = startId; id <= endId; id++) {
            try {
                // Re-use the logic from getTempleInfo to extract and save/update
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
                String speciality = extractField(doc, "Speciality");
                String prayers = extractField(doc, "Prayers");
                String thanksGiving = extractField(doc, "Thanks giving");
                String greatness = extractField(doc, "Greatness");
                String history = extractField(doc, "History");
                String features = extractField(doc, "Features");

                Double hfLat = null;
                Double hfLan = null;

                try {
                    String latStr = extractValueBySelector(doc, "input#hfLat", "value");
                    if (latStr != null && !latStr.isEmpty()) {
                        hfLat = Double.valueOf(latStr);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Could not parse hfLat as Double for ID " + id + ": " + e.getMessage());
                }

                try {
                    String lanStr = extractValueBySelector(doc, "input#hfLan", "value");
                    if (lanStr != null && !lanStr.isEmpty()) {
                        hfLan = Double.valueOf(lanStr);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Could not parse hfLan as Double for ID " + id + ": " + e.getMessage());
                }

                String location = extractField(doc, "Location");
                String nearByAirport = extractField(doc, "Near By Airport");
                String nearByRailwayStation = extractField(doc, "Near By Railway Station");
                String accommodation = extractField(doc, "Accomodation");


                Temple temple = new Temple(
                    id, name, moolavar, urchavar, ammanThayar, thalaVirutcham, theertham,
                    agamamPooja, oldYear, historicalName, city, district, state, singers,
                    festival, generalInformation, address, phone, openingTime, speciality,
                    prayers, thanksGiving, greatness, history, features, hfLat, hfLan,
                    location, nearByAirport, nearByRailwayStation, accommodation
                );

                System.out.println("Batch extracted Temple info for ID " + id + ": " + temple);

                try {
                    templeService.saveOrUpdateTemple(temple);
                    successCount++;
                } catch (DataAccessException e) {
                    System.err.println("Error saving/updating temple with ID " + id + " in database: " + e.getMessage());
                    errorCount++;
                }

            } catch (IOException e) {
                System.err.println("Skipping ID " + id + " due to web fetching error: " + e.getMessage());
                skipCount++;
            } catch (Exception e) {
                System.err.println("An unexpected error occurred for ID " + id + ": " + e.getMessage());
                e.printStackTrace();
                errorCount++;
            }
        }

        results.put("status", "Batch processing completed.");
        results.put("successful_extractions", String.valueOf(successCount));
        results.put("skipped_ids_due_to_web_error", String.valueOf(skipCount));
        results.put("failed_to_save_or_unexpected_error", String.valueOf(errorCount));
        return new ResponseEntity<>(results, HttpStatus.OK);
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
        System.err.println("extractField: Searching for label '" + label + "'");
        // Strategy 1: Table row pattern <td>Label</td><td>:</td><td>Value</td>
        String value = extractValueFromTableRow(doc, label);
        if (value != null) {
            System.err.println("extractField: Found '" + label + "' with value (Strategy 1): " + value);
            return value;
        }

        // Strategy 2: Section pattern (Label in span.topic/span.subhead, content in newsdetails below)
        value = extractSectionContent(doc, label);
        if (value != null) {
            System.err.println("extractField: Found '" + label + "' with value (Strategy 2): " + value);
            return value;
        }

        System.err.println("extractField: Label '" + label + "' not found.");
        return null;
    }

    private String extractSectionContent(Document doc, String label) {
        Element labelElement = findElementByText(doc, "span.topic, span.subhead, td.style8, td.style2", label);
        if (labelElement == null) {
            System.err.println("extractSectionContent: Label element for '" + label + "' not found.");
            return null;
        }
        System.err.println("extractSectionContent: Found label element for '" + label + "': " + labelElement.outerHtml());


        // Try Strategy 1: Look for content immediately after the label in the same parent element
        String content = extractContentAfterLabel(labelElement);
        if (content != null) {
            System.err.println("extractSectionContent: Found '" + label + "' with value (direct sibling): " + content);
            return content;
        }

        // Try Strategy 2: Table row pattern (if labelElement is in a row)
        Element parentRow = labelElement.closest("tr");
        if (parentRow != null) {
            System.err.println("extractSectionContent: Label element '" + label + "' is in a table row.");
            // Check for value in the same row
            Elements sameRowValue = parentRow.select("td.style5, td.newsdetails");
            for (Element v : sameRowValue) {
                if (!v.text().trim().isEmpty() && !v.text().toLowerCase().contains(label.toLowerCase())) {
                    System.err.println("extractSectionContent: Found '" + label + "' with value (same row): " + v.text().trim());
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
                        System.err.println("extractSectionContent: Found '" + label + "' with value (next row): " + text);
                        return text;
                    }
                }
                // Stop if we hit another significant label
                if (!nextRow.select("span.topic, span.subhead").isEmpty()) {
                    if (!nextRow.text().toLowerCase().contains(label.toLowerCase())) { // Ensure it's not the same label repeated
                        System.err.println("extractSectionContent: Stopping next row search for '" + label + "' due to new label: " + nextRow.text());
                        break;
                    }
                }
                nextRow = nextRow.nextElementSibling();
            }
        }
        System.err.println("extractSectionContent: Content for '" + label + "' not found in section strategy.");
        return null;
    }

    private String extractContentAfterLabel(Element labelElement) {
        System.err.println("extractContentAfterLabel: Checking siblings for content after '" + labelElement.text() + "'");
        // Look for a sibling span.newsdetails after the label or after a <br>
        Element sibling = labelElement.nextElementSibling();
        while (sibling != null) {
            System.err.println("extractContentAfterLabel: Checking sibling: " + sibling.outerHtml());
            if (sibling.tagName().equals("span") && sibling.hasClass("newsdetails")) {
                // Get the HTML content, replace <br> with ", ", then extract text
                String htmlContent = sibling.html();
                String processedText = Jsoup.parse(htmlContent.replace("<br>", ", ")).text().trim();
                // Clean up leading/trailing commas and multiple spaces
                processedText = processedText.replaceAll(",\\s*$", "").replaceAll("^\\s*,", "").replaceAll(",\\s*,", ",").trim();
                System.err.println("extractContentAfterLabel: Found newsdetails span and processed: " + processedText);
                return processedText;
            }
            // Skip <br> tags and continue searching
            if (sibling.tagName().equals("br")) {
                System.err.println("extractContentAfterLabel: Skipping <br> tag.");
                sibling = sibling.nextElementSibling();
                continue;
            }
            break; // Stop if it's another type of element
        }
        System.err.println("extractContentAfterLabel: No newsdetails span found directly after label.");
        return null;
    }

    private Element findElementByText(Document doc, String selector, String text) {
        System.err.println("findElementByText: Searching for selector '" + selector + "' with text '" + text + "'");
        Elements elements = doc.select(selector);
        for (Element el : elements) {
            String elText = el.text().trim().toLowerCase();
            String searchLabel = text.toLowerCase();
            System.err.println("findElementByText: Checking element text: '" + elText + "' against '" + searchLabel + "'");
            if (elText.contains(searchLabel)) {
                System.err.println("findElementByText: Found matching element: " + el.outerHtml());
                return el;
            }
        }
        System.err.println("findElementByText: No element found for selector '" + selector + "' containing text '" + text + "'");
        return null;
    }

    private String extractValueFromTableRow(Document doc, String label) {
        System.err.println("extractValueFromTableRow: Searching for label '" + label + "' in table row strategy.");
        Element labelCell = findElementByText(doc, "td.style8, td.style2, td.subhead", label);
        if (labelCell == null) {
            System.err.println("extractValueFromTableRow: Label cell for '" + label + "' not found.");
            return null;
        }
        System.err.println("extractValueFromTableRow: Found label cell for '" + label + "': " + labelCell.outerHtml());

        Element colonCell = labelCell.nextElementSibling();
        if (colonCell != null && colonCell.text().trim().contains(":")) {
            System.err.println("extractValueFromTableRow: Found colon cell: " + colonCell.outerHtml());
            Element valueCell = colonCell.nextElementSibling();
            if (valueCell != null && !valueCell.text().trim().isEmpty()) {
                System.err.println("extractValueFromTableRow: Found value cell: " + valueCell.outerHtml() + " with value: " + valueCell.text().trim());
                return valueCell.text().trim();
            }
        }
        
        // Fallback for cases where colon is in the same cell or missing
        String cellText = labelCell.text().trim();
        if (cellText.contains(":") && cellText.indexOf(":") < cellText.length() - 1) {
            String extractedValue = cellText.substring(cellText.indexOf(":") + 1).trim();
            System.err.println("extractValueFromTableRow: Found value via fallback (colon in same cell): " + extractedValue);
            return extractedValue;
        }
        System.err.println("extractValueFromTableRow: Value for '" + label + "' not found in table row strategy.");
        return null;
    }
}
