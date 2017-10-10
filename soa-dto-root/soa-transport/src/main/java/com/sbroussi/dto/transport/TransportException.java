package com.sbroussi.dto.transport;

/**
 * Technical Exception thrown when sending a message.
 */
public class TransportException extends RuntimeException {

    public TransportException(final String message) {
        super(message);
    }

    public TransportException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
