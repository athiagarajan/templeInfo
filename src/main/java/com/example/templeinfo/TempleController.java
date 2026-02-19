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
            String name = null;
            Elements nameElements = doc.select("*:contains(Sri Chidambaram thillai natarajar temple)"); // Generic selector
            if (!nameElements.isEmpty()) {
                // Try to find the element that most closely matches the name as its own text
                for (Element el : nameElements) {
                    if (el.text().trim().equals("Sri Chidambaram thillai natarajar temple")) {
                        name = el.text().trim();
                        break;
                    }
                }
                if (name == null) { // Fallback if exact match not found
                    name = nameElements.first().text().trim(); // Take the text of the first found element
                }
            }
            // --- End Name Extraction ---


            String moolavar = extractValueFromTableRow(doc, "Moolavar"); // Label is "Moolavar"
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
                state
            );

            System.out.println("Temple info extracted using Jsoup selectors: " + temple); // Keep final output log

            return temple;
        } catch (IOException e) {
            System.err.println("Error during Jsoup URL connection or selector extraction: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to get temple info due to Jsoup error", e);
        }
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
