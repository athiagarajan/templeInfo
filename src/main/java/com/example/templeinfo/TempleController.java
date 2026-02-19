package com.example.templeinfo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.regex.Pattern;

@RestController
public class TempleController {

    private final WebPageFetcher webPageFetcher;

    @Autowired
    public TempleController(WebPageFetcher webPageFetcher) {
        this.webPageFetcher = webPageFetcher;
    }

    @PostMapping("/temple-info")
    public Temple getTempleInfo(@RequestBody TempleUrlRequest request) throws IOException {
        try {
            String url = request.getUrl();
            Document doc = webPageFetcher.fetch(url); // Now returns Jsoup Document

            // --- Name Extraction ---
            String name = extractWithSelector(doc, "span#HyperLink2");
            if (name == null) {
                name = extractWithSelector(doc, "span.subhead");
            }
            // --- End Name Extraction ---

            String moolavar = extractValueFromTableRow(doc, "Moolavar");
            String urchavar = extractValueFromTableRow(doc, "Urchavar");
            String ammanThayar = extractValueFromTableRow(doc, "Amman / Thayar");
            String thalaVirutcham = extractValueFromTableRow(doc, "Thala Virutcham");
            String theertham = extractValueFromTableRow(doc, "Theertham");
            String agamamPooja = extractValueFromTableRow(doc, "Agamam / Pooja");
            String oldYear = extractValueFromTableRow(doc, "Old year");
            String historicalName = extractValueFromTableRow(doc, "Historical Name");
            String city = extractValueFromTableRow(doc, "City");
            String district = extractValueFromTableRow(doc, "District");
            String state = extractValueFromTableRow(doc, "State");

            String singers = extractSectionContent(doc, "Singers");
            String festival = extractSectionContent(doc, "Festival");
            String generalInformation = extractSectionContent(doc, "General Information");
            String address = extractSectionContent(doc, "Address");
            String phone = extractSectionContent(doc, "Phone");
            String openingTime = extractSectionContent(doc, "Opening Time");
            String speciality = extractSectionContent(doc, "Temple's Speciality");
            String prayers = extractSectionContent(doc, "Prayers");
            String thanksGiving = extractSectionContent(doc, "Thanks giving");
            String greatness = extractSectionContent(doc, "Greatness Of Temple");
            String history = extractSectionContent(doc, "Temple History");
            String features = extractSectionContent(doc, "Special Features");

            Temple temple = new Temple(
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
                features
            );

            System.out.println("Temple info extracted: " + temple);

            return temple;
        } catch (IOException e) {
            System.err.println("Error during extraction: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to get temple info", e);
        }
    }

    /**
     * Extracts content from a section like "Singers", "Festival", or "General Information".
     */
    private String extractSectionContent(Document doc, String label) {
        // Find the label span manually to avoid issues with special characters in :contains()
        Element labelSpan = findElementByText(doc, "span.topic", label);
        if (labelSpan == null) {
            labelSpan = findElementByText(doc, "span.subhead", label);
        }
        if (labelSpan == null) return null;

        // Try finding the content in the next td.newsdetails (most common for long text)
        Element current = labelSpan.closest("tr");
        if (current != null) {
            Element nextRow = current.nextElementSibling();
            int rowLimit = 15; 
            while (nextRow != null && rowLimit-- > 0) {
                Element newsDetail = nextRow.selectFirst("td.newsdetails");
                if (newsDetail != null && !newsDetail.text().trim().isEmpty()) {
                    return newsDetail.text().trim();
                }
                // If we hit another topic or subhead span that isn't the current one, we've likely passed the content
                if (!nextRow.select("span.topic, span.subhead").isEmpty()) {
                     // But only if it's not just the label we're looking at again
                     if (!nextRow.text().contains(label)) {
                        break;
                     }
                }
                nextRow = nextRow.nextElementSibling();
            }
        }

        // Fallback: check the parent cell or next cell
        Element parentTd = labelSpan.closest("td");
        if (parentTd != null) {
            Element nextTd = parentTd.nextElementSibling();
            if (nextTd != null && !nextTd.text().trim().isEmpty()) {
                return nextTd.text().trim();
            }
            // Check own text of the parent cell (if text follows span directly)
            String ownText = parentTd.ownText().trim();
            if (!ownText.isEmpty()) return ownText;
        }

        return null;
    }

    private Element findElementByText(Document doc, String selector, String text) {
        Elements elements = doc.select(selector);
        for (Element el : elements) {
            if (el.text().contains(text)) {
                return el;
            }
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
     * Extracts value from a table row pattern: `<td>Label</td><td>:</td><td>Value</td>`
     */
    private String extractValueFromTableRow(Document doc, String label) {
        // Find the td element that contains the label text (checks children)
        Elements labelCells = doc.select(String.format("td.style8:contains(%s)", label)); // Uses :contains()

        for (Element labelCell : labelCells) {
            // Assume this labelCell is the correct one, and proceed to its siblings
            Element colonCell = labelCell.nextElementSibling();
            if (colonCell != null && colonCell.text().trim().equals(":")) {
                Element valueCell = colonCell.nextElementSibling();
                if (valueCell != null && !valueCell.text().trim().isEmpty()) {
                    String value = valueCell.text().trim();
                    return value;
                }
            }
        }
        return null; // Value not found
    }
}
