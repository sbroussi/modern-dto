package com.sbroussi.dto;

public class DtoException extends RuntimeException {

    public DtoException(String message) {
        super(message);
    }

    public DtoException(String message, Throwable cause) {
        super(message, cause);
    }
}
