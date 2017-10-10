package com.sbroussi.soa.dialect;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoFormatter;
import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;
import com.sbroussi.soa.dialect.zos.ZosFormatter;
import com.sbroussi.soa.dialect.zos.ZosParser;

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
    public void formatToJmsText(final SoaContext jmsContext, final SoaDtoRequest request) {
        final DtoContext dtoContext = jmsContext.getDtoContext();
        final DtoFormatter formatter = dtoContext.getDtoFormatter();

        // format DTO with fixed-width fields
        final String rawData = formatter.format(request.getRequestDto());

        // insert z/OS header prefix
        String rawJms = zosFormatter.formatWithHeader(request, channel, rawData);

        request.setRawRequest(rawJms);
    }

    @Override
    public void parseFromJmsText(final SoaContext jmsContext, final SoaDtoRequest request) {
        zosParser.parse(jmsContext.getDtoContext(), request);

    }


}
