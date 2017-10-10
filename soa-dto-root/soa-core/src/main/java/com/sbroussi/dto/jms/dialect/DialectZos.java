package com.sbroussi.dto.jms.dialect;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoFormatter;
import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;
import com.sbroussi.dto.jms.dialect.zos.ZosFormatter;
import com.sbroussi.dto.jms.dialect.zos.ZosParser;

/**
 * Dialect: z/OS fixed-width fields with header.
 */
public class DialectZos implements Dialect {

    private ZosFormatter zosFormatter = new ZosFormatter();
    private ZosParser zosParser = new ZosParser();

    /**
     * The channel identifier required by the back-end system (@PORTAL, @WEB, @ESB...).
     */
    private String channel;

    /**
     * Constructor.
     *
     * @param channel the channel identifier required by the back-end system (@PORTAL, @WEB, @ESB...).
     */
    public DialectZos(final String channel) {
        this.channel = channel;

    }


    @Override
    public void formatToJmsText(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        final DtoContext dtoContext = jmsContext.getDtoContext();
        final DtoFormatter formatter = dtoContext.getDtoFormatter();

        // format DTO with fixed-width fields
        final String rawData = formatter.format(request.getRequestDto());

        // insert z/OS header prefix
        String rawJms = zosFormatter.formatWithHeader(request, channel, rawData);

        request.setRawRequest(rawJms);
    }

    @Override
    public void parseFromJmsText(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        zosParser.parse(jmsContext.getDtoContext(), request);

    }


}
