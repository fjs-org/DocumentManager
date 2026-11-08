package com.documentmanager.exception;

public class FileSizeExceededException extends RuntimeException {

    public FileSizeExceededException() {
        super("File size exceeds the maximum allowed size of 10 MB");
    }
}