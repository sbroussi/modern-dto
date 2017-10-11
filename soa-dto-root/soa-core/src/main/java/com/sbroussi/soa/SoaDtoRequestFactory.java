package com.sbroussi.soa;

import lombok.Getter;

/**
 * Factory to create a 'SoaDtoRequest' with a 'prepared' SoaContext.
 */

public class SoaDtoRequestFactory {

    /**
     * The SOA Context to use for this request.
     */
    @Getter
    private final SoaContext soaContext;

    public SoaDtoRequestFactory(final SoaContext soaContext) {
        this.soaContext = soaContext;
    }

    /**
     * @param requestDto the request object to send
     * @return a new instance of SoaDtoRequest with a 'prepared' SoaContext.
     */
    public SoaDtoRequest create(final Object requestDto) {
        return new SoaDtoRequest(soaContext, requestDto);
    }
}
