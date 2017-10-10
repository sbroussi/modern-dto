package com.sbroussi.soa.dialect;

import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;

/**
 * Dialect: JSON format.
 */

public class DialectJson implements Dialect {

    @Override
    public void formatToJmsText(final SoaContext jmsContext, final SoaDtoRequest request) {

        // TODO: not implemented yet
        throw new IllegalStateException("TODO: not implemented yet");

    }

    @Override
    public void parseFromJmsText(final SoaContext jmsContext, final SoaDtoRequest request) {

        // TODO: not implemented yet
        throw new IllegalStateException("TODO: not implemented yet");

    }
}
