package com.sbroussi.dto.jms;

/**
 * Technical Exception thrown when sending a JMS mesages.
 */
public class JmsException extends RuntimeException {

    public JmsException(final String message) {
        super(message);
    }

    public JmsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
