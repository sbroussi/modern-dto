package com.sbroussi.dto.jms.dialect;

import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;

/**
 * Dialect: JSON format.
 */

public class DialectJson implements Dialect {

    @Override
    public void formatToJmsText(DtoJmsContext jmsContext, DtoJmsRequest request) {

        // TODO: not implemented yet
        throw new IllegalStateException("TODO: not implemented yet");

    }

    @Override
    public void parseFromJmsText(DtoJmsContext jmsContext, DtoJmsRequest request) {

        // TODO: not implemented yet
        throw new IllegalStateException("TODO: not implemented yet");

    }
}
