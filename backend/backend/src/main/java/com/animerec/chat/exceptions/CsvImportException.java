package com.animerec.chat.exceptions;

public class CsvImportException extends RuntimeException {
    public CsvImportException(String message) {
        super(message);
    }

    public CsvImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
