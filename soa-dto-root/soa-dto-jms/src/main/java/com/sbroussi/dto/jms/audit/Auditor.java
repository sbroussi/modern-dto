package com.sbroussi.dto.jms.audit;

import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;

/**
 * Auditor to keep a trace of all sent and received messages.
 */
public interface Auditor {

    /**
     * Trace step: Before the JMS REQUEST is sent.
     *
     * @param jmsContext the current JMS context
     * @param request    the DtoJmsRequest, with the 'rawRequest' populated
     */
    void traceBeforeRequest(DtoJmsContext jmsContext, DtoJmsRequest request);

    /**
     * Trace step: After the JMS REQUEST is sent and the RAW JMS RESPONSE is read (if any).
     *
     * @param jmsContext the current JMS context
     * @param request    the DtoJmsRequest, with the 'rawResponse' populated
     */
    void traceAfterRequest(DtoJmsContext jmsContext, DtoJmsRequest request);

    /**
     * Trace step: After the RAW JMS RESPONSE has been parsed and Response Objects
     * are populated in the 'DtoJmsResponse' (if any).
     * <p>
     * This method is not called for One-Way requests.
     *
     * @param jmsContext the current JMS context
     * @param request    the DtoJmsRequest, with the 'rawResponse' populated
     */
    void traceAfterResponseParsing(DtoJmsContext jmsContext, DtoJmsRequest request);

}
