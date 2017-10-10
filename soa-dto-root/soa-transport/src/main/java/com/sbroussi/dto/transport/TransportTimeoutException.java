package com.sbroussi.dto.transport;

/**
 * Technical Exception thrown when timeout delay is reached and no response is read.
 */
public class TransportTimeoutException extends TransportException {

    public TransportTimeoutException(String message) {
        super(message);
    }
}
