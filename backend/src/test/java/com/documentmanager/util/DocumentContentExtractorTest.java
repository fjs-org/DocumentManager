package com.documentmanager.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentContentExtractorTest {

    private final DocumentContentExtractor extractor = new DocumentContentExtractor();

    @Test
    void extractsTextFromDocument() {
        byte[] data = "Hello Tika".getBytes(StandardCharsets.UTF_8);

        String content = extractor.extractText(data);

        assertTrue(content.contains("Hello Tika"));
    }
}
