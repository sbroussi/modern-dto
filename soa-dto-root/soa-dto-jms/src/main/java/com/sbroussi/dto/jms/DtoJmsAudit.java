package com.sbroussi.dto.jms;

public interface DtoJmsAudit {

    /**
     * @param jmsContext the current JMS context
     * @param request    the DtoJmsRequest
     */
    void traceRequest(DtoJmsContext jmsContext, DtoJmsRequest request);

}
