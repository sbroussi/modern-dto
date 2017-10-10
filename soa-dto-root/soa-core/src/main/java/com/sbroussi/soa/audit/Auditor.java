package com.sbroussi.soa.audit;

import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;

/**
 * Auditor to keep a trace of all sent and received messages.
 */
public interface Auditor {

    /**
     * Trace step: Before the JMS REQUEST is sent.
     *
     * @param jmsContext the current JMS context
     * @param request    the SoaDtoRequest, with the 'rawRequest' populated
     */
    void traceBeforeRequest(SoaContext jmsContext, SoaDtoRequest request);

    /**
     * Trace step: After the JMS REQUEST is sent and the RAW JMS RESPONSE is read (if any).
     *
     * @param jmsContext the current JMS context
     * @param request    the SoaDtoRequest, with the 'rawResponse' populated
     */
    void traceAfterRequest(SoaContext jmsContext, SoaDtoRequest request);

    /**
     * Trace step: After the RAW JMS RESPONSE has been parsed and Response Objects
     * are populated in the 'SoaDtoResponse' (if any).
     * <p>
     * This method is not called for One-Way requests.
     *
     * @param jmsContext the current JMS context
     * @param request    the SoaDtoRequest, with the 'rawResponse' populated
     */
    void traceAfterResponseParsing(SoaContext jmsContext, SoaDtoRequest request);

}
