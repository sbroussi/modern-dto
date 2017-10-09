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
 * - MessageSenderSpringImpl.java: an implementation based on Spring-JMS (jmsTemplate).
 */
public interface MessageSender {

    /**
     * Sends a JMS message and optionally collect the RAW text of JMS response (if any).
     * <p>
     * The implementation should use 'correlationId' to send/receive JMS Request-Response synchronously.
     *
     * @param jmsContext the DTO JMS context
     * @param request    The DTO request wrapper. The responses are populated in this bean.
     */
    void sendMessage(final DtoJmsContext jmsContext, final DtoJmsRequest request);
}
