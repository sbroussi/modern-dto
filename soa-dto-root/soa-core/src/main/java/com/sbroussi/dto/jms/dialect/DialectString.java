package com.sbroussi.dto.jms.dialect;

import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;

/**
 * Dialect: simple String format.
 * <p>
 * FORMAT: use 'getRequestDto().toString()'
 * <p>
 * PARSE: nothing to do, read the 'rawResponse' property of the DtoJmsRequest.
 */

public class DialectString implements Dialect {

    @Override
    public void formatToJmsText(final DtoJmsContext jmsContext, final DtoJmsRequest request) {

        // format DTO using 'dto.toString()'
        final Object dto = request.getRequestDto();
        final String rawData = (dto == null) ? "" : dto.toString();

        request.setRawRequest(rawData);

    }

    @Override
    public void parseFromJmsText(final DtoJmsContext jmsContext, final DtoJmsRequest request) {

        // nothing to do, the 'rawResponse' property of the DtoJmsRequest is already set

    }
}
