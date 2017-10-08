package com.sbroussi.dto.jms;

public interface MessageSender {

    /**
     * Sends a message and collect all the RAW text responses.
     * <p>
     * The implementation should use 'correlationId' to send/receive JMS Request-Response synchronously.
     *
     * @param jmsContext the DTO JMS context
     * @param request    The DTO request wrapper. The responses are populated in this bean.
     */
    void sendMessage(final DtoJmsContext jmsContext, final DtoJmsRequest request);
}
