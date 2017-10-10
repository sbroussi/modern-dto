package com.sbroussi.soa.dialect;

import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;

/**
 * Dialect: simple String format.
 * <p>
 * FORMAT: use 'getRequestDto().toString()'
 * <p>
 * PARSE: nothing to do, read the 'rawResponse' property of the SoaDtoRequest.
 */

public class DialectString implements Dialect {

    @Override
    public void formatToJmsText(final SoaContext jmsContext, final SoaDtoRequest request) {

        // format DTO using 'dto.toString()'
        final Object dto = request.getRequestDto();
        final String rawData = (dto == null) ? "" : dto.toString();

        request.setRawRequest(rawData);

    }

    @Override
    public void parseFromJmsText(final SoaContext jmsContext, final SoaDtoRequest request) {

        // nothing to do, the 'rawResponse' property of the SoaDtoRequest is already set

    }
}
