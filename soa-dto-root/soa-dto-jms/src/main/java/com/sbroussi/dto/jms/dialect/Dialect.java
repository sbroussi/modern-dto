package com.sbroussi.dto.jms.dialect;

import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;

/**
 * The 'dialect' defines how the JMS messages must be formatted or parsed.
 * <p>
 * For example:
 * <p>
 * - for custom legacy back-end systems with headers and credentials transmitted in the JMS message text,
 * <p>
 * - JSON formatted messages,
 * <p>
 * - XML formatted messages,
 */
public interface Dialect {

    /**
     * The implementation must set the 'rawRequest' property of the DtoJmsRequest.
     *
     * @param jmsContext the DTO JMS context
     * @param request    The DTO request wrapper.
     */
    void formatToJmsText(final DtoJmsContext jmsContext, final DtoJmsRequest request);

    /**
     * The implementation must parse the 'rawResponse' property of the DtoJmsRequest and
     * populate returned Objects in the 'DtoJmsResponse' of the 'DtoJmsRequest'.
     *
     * @param jmsContext the DTO JMS context
     * @param request    The DTO request wrapper.
     */
    void parseFromJmsText(final DtoJmsContext jmsContext, final DtoJmsRequest request);

}
