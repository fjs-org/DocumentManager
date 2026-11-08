package com.documentmanager.util;

import com.documentmanager.exception.DocumentContentExtractionException;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class DocumentContentExtractor {

    private final Tika tika = new Tika();

    public String extractText(byte[] data) {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            return tika.parseToString(inputStream);
        } catch (IOException | TikaException ex) {
            throw new DocumentContentExtractionException("Failed to extract content from document", ex);
        }
    }
}
