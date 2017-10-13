package com.sbroussi.soa.audit;

import com.sbroussi.soa.SoaDtoRequest;

import java.util.Map;

/**
 * Auditor to keep a trace of all sent and received messages.
 */
public interface Auditor {

    /**
     * Trace step: Before sending the REQUEST.
     *
     * @param request the SoaDtoRequest, with the 'rawRequest' populated
     * @param data    a Map to store objects that can be re-used during the whole audit process
     *                <p>
     *                The auditor should store one object with its classname as 'key' of the map.
     */
    void traceBefore(SoaDtoRequest request, Map<String, Object> data);

    /**
     * Trace step: After the REQUEST is sent and the RESPONSE is received and parsed (if any)
     * or if an Exception was thrown during the call.
     * <p>
     * If a response has been received and parsed, the Response Objects
     * are populated in the 'SoaDtoResponse' (if any).
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
    void traceAfter(SoaDtoRequest request, Map<String, Object> data, Throwable cause);


}
