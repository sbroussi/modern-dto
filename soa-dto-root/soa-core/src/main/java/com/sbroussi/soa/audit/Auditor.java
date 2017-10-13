package com.sbroussi.soa.audit;

import com.sbroussi.soa.SoaDtoRequest;

import java.util.Map;

/**
 * Auditor to keep a trace of all sent and received messages.
 */
public interface Auditor {

    /**
     * Trace step: Before the REQUEST is sent.
     *
     * @param request the SoaDtoRequest, with the 'rawRequest' populated
     * @param data    a Map to store objects that can be re-used during the whole audit process
     *                <p>
     *                The auditor should store one object with its classname as 'key' of the map.
     */
    void traceBeforeRequest(SoaDtoRequest request, Map<String, Object> data);


    /**
     * Trace step: After the REQUEST is sent and the RAW RESPONSE is read (if any).
     *
     * @param request the SoaDtoRequest, with the 'rawResponse' populated
     * @param data    a Map to store objects that can be re-used during the whole audit process
     *                <p>
     *                The auditor should store one object with its classname as 'key' of the map.
     */
    void traceAfterRequest(SoaDtoRequest request, Map<String, Object> data);


    /**
     * Trace step: After the RAW RESPONSE has been parsed and Response Objects
     * are populated in the 'SoaDtoResponse' (if any).
     * <p>
     * This method is not called for One-Way requests.
     *
     * @param request the SoaDtoRequest, with the 'rawResponse' populated
     * @param data    a Map to store objects that can be re-used during the whole audit process
     *                <p>
     *                The auditor should store one object with its classname as 'key' of the map.
     */
    void traceAfterResponseParsing(SoaDtoRequest request, Map<String, Object> data);

    /**
     * Trace step: the final call to the auditor (try/catch/finally meaning).
     * <p>
     * Note: This is the LAST call of the auditor for this request that was successful or failed.
     *
     * @param request the SoaDtoRequest, with the 'rawResponse' populated
     * @param data    a Map to store objects that can be re-used during the whole audit process
     *                <p>
     *                The auditor should store one object with its classname as 'key' of the map.
     * @param cause   Optional: the last error returned during sending the request or
     *                receiving or parsing the response.
     *                <p>
     *                The auditor should NOT re-throw exception, the sender will do that later on.
     */
    void traceFinally(SoaDtoRequest request, Map<String, Object> data, Throwable cause);


}
