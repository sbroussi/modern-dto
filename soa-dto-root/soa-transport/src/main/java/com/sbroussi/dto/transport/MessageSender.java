package com.sbroussi.dto.transport;

/**
 * Sends a message and optionally collect the RAW text of response (if any).
 * <p>
 * Current implementations are:
 * <p>
 * - SenderJms.java: a simple JMS connector.
 * <p>
 * - SenderSpringJms.java: an implementation based on Spring-JMS (with JmsTemplate).
 * <p>
 * - possible extension: HTTP connector to Rest API/Json
 */
public interface MessageSender {

    /**
     * Sends a JMS message without expecting a response (One-Way request).
     *
     * @param message The RAW text message to send using JMS
     * @throws TransportException if I/O error occurs
     */
    void send(String message) throws TransportException;

    /**
     * Sends a JMS message and optionally collect the RAW text of JMS response (if any).
     * <p>
     * The implementation should use 'correlationId' to send/receive JMS Request-Response synchronously.
     *
     * @param message The RAW text message to send using JMS
     * @param timeout the duration to wait for a response (in milliseconds)
     * @return The RAW text message of the response (if any)
     * @throws TransportException        if I/O error occurs
     * @throws TransportTimeoutException if timeout delay is reached and no response is read
     */
    String sendAndReceive(String message, long timeout) throws TransportException, TransportTimeoutException;

}
