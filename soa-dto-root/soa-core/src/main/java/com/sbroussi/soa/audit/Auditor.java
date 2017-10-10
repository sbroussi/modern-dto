package com.sbroussi.soa.audit;

import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;

/**
 * Auditor to keep a trace of all sent and received messages.
 */
public interface Auditor {

    /**
     * Trace step: Before the REQUEST is sent.
     *
     * @param soaContext the current SOA context
     * @param request    the SoaDtoRequest, with the 'rawRequest' populated
     */
    void traceBeforeRequest(SoaContext soaContext, SoaDtoRequest request);

    /**
     * Trace step: After the REQUEST is sent and the RAW RESPONSE is read (if any).
     *
     * @param soaContext the current SOA context
     * @param request    the SoaDtoRequest, with the 'rawResponse' populated
     */
    void traceAfterRequest(SoaContext soaContext, SoaDtoRequest request);

    /**
     * Trace step: After the RAW RESPONSE has been parsed and Response Objects
     * are populated in the 'SoaDtoResponse' (if any).
     * <p>
     * This method is not called for One-Way requests.
     *
     * @param soaContext the current SOA context
     * @param request    the SoaDtoRequest, with the 'rawResponse' populated
     */
    void traceAfterResponseParsing(SoaContext soaContext, SoaDtoRequest request);

}
