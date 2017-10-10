package com.sbroussi.dto.jms;

/**
 * Sends a JMS message and optionally collect the RAW text of JMS response (if any).
 * <p>
 * The implementation should use 'correlationId' to send/receive JMS Request-Response synchronously.
 * <p>
 * Current implementations are:
 * <p>
 * - MessageSenderImpl.java: a simple JMS connector.
 * <p>
 * - MessageSenderSpringImpl.java: an implementation based on Spring-JMS (with JmsTemplate).
 */
public interface MessageSender {

    /**
     * Sends a JMS message without expecting a response (One-Way request).
     *
     * @param message The RAW text message to send using JMS
     */
    void send(String message);

    /**
     * Sends a JMS message and optionally collect the RAW text of JMS response (if any).
     * <p>
     * The implementation should use 'correlationId' to send/receive JMS Request-Response synchronously.
     *
     * @param message The RAW text message to send using JMS
     * @param timeout the duration to wait for a response (in milliseconds)
     * @return The RAW text message of the response (if any)
     */
    String sendAndReceive(String message, long timeout);

}
