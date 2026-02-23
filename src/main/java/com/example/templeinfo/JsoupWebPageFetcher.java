package com.example.templeinfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsoupWebPageFetcher implements WebPageFetcher {
    @Override
    public Document fetch(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        System.err.println("Fetched HTML for URL: " + url);
        return doc;
    }
}
