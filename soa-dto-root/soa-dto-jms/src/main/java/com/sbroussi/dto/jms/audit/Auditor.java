package com.sbroussi.dto.jms.audit;

import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;

/**
 * Auditor to keep a trace of all sent and received messages.
 */
public interface Auditor {

    /**
     * Trace before the JMS REQUEST is sent.
     *
     * @param jmsContext the current JMS context
     * @param request    the DtoJmsRequest
     */
    void traceBeforeRequest(DtoJmsContext jmsContext, DtoJmsRequest request);

    /**
     * Trace after the JMS RESPONSE is read (or when the One-Way request has been sent).
     *
     * @param jmsContext the current JMS context
     * @param request    the DtoJmsRequest, with the 'rawResponse' populated
     */
    void traceAfterResponse(DtoJmsContext jmsContext, DtoJmsRequest request);

}