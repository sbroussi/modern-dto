package com.sbroussi.soa.zos;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoFormatter;
import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;
import com.sbroussi.soa.dialect.Dialect;

/**
 * Dialect: z/OS fixed-width fields with header.
 */
public class ZosDialect implements Dialect {

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
    public ZosDialect(final String channel) {
        this.channel = channel;

    }


    @Override
    public void formatToRequestMessage(final SoaDtoRequest request) {
        final SoaContext soaContext = request.getSoaContext();
        final DtoContext dtoContext = soaContext.getDtoContext();
        final DtoFormatter formatter = dtoContext.getDtoFormatter();

        // format DTO with fixed-width fields
        final String rawData = formatter.format(request.getRequestDto());

        // insert z/OS header prefix
        String rawRequest = zosFormatter.formatWithHeader(request, channel, rawData);

        request.setRawRequest(rawRequest);
    }

    @Override
    public void parseFromResponseMessage(final SoaDtoRequest request) {
        final SoaContext soaContext = request.getSoaContext();
        zosParser.parse(soaContext.getDtoContext(), request);

    }


}
