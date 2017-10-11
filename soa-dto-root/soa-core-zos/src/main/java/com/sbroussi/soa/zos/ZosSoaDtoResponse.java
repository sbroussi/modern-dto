package com.sbroussi.soa.zos;

import com.sbroussi.soa.SoaDtoResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This Java Bean contains all the response information.
 */
@Getter
@Setter
public class ZosSoaDtoResponse extends SoaDtoResponse {

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
     * The Map of the DTO found in the response.
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
