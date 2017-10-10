package com.sbroussi.soa.dialect;

import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;

/**
 * Dialect: JSON format.
 */

public class DialectJson implements Dialect {

    @Override
    public void formatToRequestMessage(final SoaContext soaContext, final SoaDtoRequest request) {

        // TODO: not implemented yet
        throw new IllegalStateException("TODO: not implemented yet");

    }

    @Override
    public void parseFromResponseMessage(final SoaContext soaContext, final SoaDtoRequest request) {

        // TODO: not implemented yet
        throw new IllegalStateException("TODO: not implemented yet");

    }
}
