package com.sbroussi.soa.dialect;

import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;

/**
 * The 'dialect' defines how the SOA messages must be formatted or parsed.
 * <p>
 * For example:
 * <p>
 * - for custom legacy back-end systems with headers and credentials transmitted in the MQ-Series message text,
 * <p>
 * - JSON formatted messages,
 * <p>
 * - XML formatted messages,
 */
public interface Dialect {

    /**
     * The implementation must set the 'rawRequest' property of the SoaDtoRequest.
     *
     * @param soaContext the current SOA context
     * @param request    The DTO request wrapper.
     */
    void formatToRequestMessage(final SoaContext soaContext, final SoaDtoRequest request);

    /**
     * The implementation must parse the 'rawResponse' property of the SoaDtoRequest and
     * populate returned Objects in the 'SoaDtoResponse' of the 'SoaDtoRequest'.
     *
     * @param soaContext the current SOA context
     * @param request    The DTO request wrapper.
     */
    void parseFromResponseMessage(final SoaContext soaContext, final SoaDtoRequest request);

}
