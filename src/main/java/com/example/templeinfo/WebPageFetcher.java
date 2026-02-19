package com.example.templeinfo;

import org.jsoup.nodes.Document;
import java.io.IOException;

public interface WebPageFetcher {
    Document fetch(String url) throws IOException;
}
