package com.sbroussi.soa;

import lombok.Getter;
import lombok.Setter;

/**
 * This 'SoaDtoRequest' has a 'static' SOA Context that is populated to ALL instances.
 * <p>
 * You have to set call 'setStaticSoaContext'.
 * <p>
 * This class is helpful for legacy applications that cannot easily propagate a
 * reference to one SOA Context in existing code.
 */
public class SoaDtoRequestStaticContext extends SoaDtoRequest {

    /**
     * The SOA Context to use for ALL requests (static).
     */
    @Getter
    @Setter
    private static SoaContext staticSoaContext;

    // ------------------------ constructor

    /**
     * Constructor that inject the static 'SoaContext' in the super-class,
     *
     * @param requestDto the request object to send
     */
    public SoaDtoRequestStaticContext(final Object requestDto) {
        super(staticSoaContext, requestDto);
    }

}
