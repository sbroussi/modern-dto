package com.sbroussi.dto.jms;

public interface DtoJmsAudit {

    /**
     * Trace before the REQUEST is sent.
     *
     * @param jmsContext the current JMS context
     * @param request    the DtoJmsRequest
     */
    void traceBeforeRequest(DtoJmsContext jmsContext, DtoJmsRequest request);

    /**
     * Trace after the RESPONSES are read (or when the One-Way request has been sent).
     *
     * @param jmsContext the current JMS context
     * @param request    the DtoJmsRequest
     */
    void traceAfterResponse(DtoJmsContext jmsContext, DtoJmsRequest request);

}
