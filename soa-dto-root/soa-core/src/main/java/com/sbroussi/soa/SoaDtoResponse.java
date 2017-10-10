package com.sbroussi.soa;

import lombok.Getter;
import lombok.Setter;

/**
 * This Java Bean contains all the response information.
 */
@Getter
@Setter
public class SoaDtoResponse {

    /**
     * When the response was received and decoded.
     */
    private long timestampDecoded;


}
