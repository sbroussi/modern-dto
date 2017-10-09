package com.sbroussi.dto.jms.dialect.zos;

import com.sbroussi.dto.jms.DtoJmsResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This Java Bean contains all the JMS response information.
 */
@Getter
@Setter
public class ZosDtoJmsResponse extends DtoJmsResponse {

    /**
     * The channel identifier.
     */
    private String channel = null;

    /**
     * The response header.
     */
    private ZosResponseHeader header;

    /**
     * The data elements (of type ZosResponseData).
     */
    private List<ZosResponseData> zosResponses = new ArrayList<ZosResponseData>();

    /**
     * @param responseName The name of the z/OS response
     * @return the ZosResponseData or 'null' if not found
     */
    public ZosResponseData getResponse(final String responseName) {
        if (responseName == null) {
            return null;
        }

        final String key = responseName.trim(); // 'name' of zosResponseData are trimmed
        for (ZosResponseData zosResponseData : zosResponses) {
            if (key.equals(zosResponseData.getName())) {
                return zosResponseData;
            }
        }
        return null;
    }

}
