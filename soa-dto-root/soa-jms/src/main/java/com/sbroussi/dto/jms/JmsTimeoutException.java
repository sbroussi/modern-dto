package com.sbroussi.dto.jms;

/**
 * Technical Exception thrown when timeout delay is reached and no response is read.
 */
public class JmsTimeoutException extends JmsException {

    public JmsTimeoutException(String message) {
        super(message);
    }
}
