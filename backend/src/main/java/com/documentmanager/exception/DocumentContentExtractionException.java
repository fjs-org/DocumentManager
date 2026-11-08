package com.documentmanager.exception;

public class DocumentContentExtractionException extends RuntimeException {

    public DocumentContentExtractionException(String message) {
        super(message);
    }

    public DocumentContentExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
