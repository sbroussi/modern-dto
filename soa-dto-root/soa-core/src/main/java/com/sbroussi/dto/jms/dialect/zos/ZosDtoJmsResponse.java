package com.sbroussi.dto.jms.dialect.zos;

import com.sbroussi.dto.jms.DtoJmsResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * The Map of the DTO found in the JMS response.
     * <p/>
     * key: the response name (ABORT, LSTPOPUP...)
     * <p/>
     * value: the list of DTOs that have been decoded
     * <p/>
     * Implementation: use a LinkedHashMap, the iteration order is, the order in which keys were inserted.
     */
    private Map<String, List<Object>> dtoResponsesMap = null;


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
