package com.sbroussi.dto.jms.dialect;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoFormatter;
import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;

/**
 * Dialect: z/OS fixed-width fields with header.
 */
public class DialectZos implements Dialect {


    @Override
    public void formatToJmsText(DtoJmsContext jmsContext, DtoJmsRequest request) {
        final DtoContext dtoContext = jmsContext.getDtoContext();
        final DtoFormatter formatter = dtoContext.getDtoFormatter();

        // format DTO
        final String rawJms = formatter.format(request.getRequestDto());

        request.setRawRequest(rawJms);
    }

    @Override
    public void parseFromJmsText(DtoJmsContext jmsContext, DtoJmsRequest request) {

    }
}
