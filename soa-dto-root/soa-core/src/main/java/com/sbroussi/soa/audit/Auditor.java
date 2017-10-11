package com.sbroussi.soa.audit;

import com.sbroussi.soa.SoaDtoRequest;

/**
 * Auditor to keep a trace of all sent and received messages.
 */
public interface Auditor {

    /**
     * Trace step: Before the REQUEST is sent.
     *
     * @param request    the SoaDtoRequest, with the 'rawRequest' populated
     */
    void traceBeforeRequest(SoaDtoRequest request);

    /**
     * Trace step: After the REQUEST is sent and the RAW RESPONSE is read (if any).
     *
     * @param request    the SoaDtoRequest, with the 'rawResponse' populated
     */
    void traceAfterRequest(SoaDtoRequest request);

    /**
     * Trace step: After the RAW RESPONSE has been parsed and Response Objects
     * are populated in the 'SoaDtoResponse' (if any).
     * <p>
     * This method is not called for One-Way requests.
     *
     * @param request    the SoaDtoRequest, with the 'rawResponse' populated
     */
    void traceAfterResponseParsing(SoaDtoRequest request);

}
