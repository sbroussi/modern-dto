package com.sbroussi.soa.zos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for a z/OS response data.
 */
@Getter
@Setter
public class ZosResponseData {

    /**
     * The z/OS response data name.
     */
    private String name = null;

    /**
     * The z/OS response data length.
     */
    private int length = 0;

    /**
     * The request name.
     */
    private String requestName = null;

    /**
     * The entity.
     */
    private String entity = null;

    /**
     * The response block.
     */
    private int block = 0;

    /**
     * The z/OS response data number of elements.
     */
    private int nbElements = 0;

    /**
     * The list of response data elements.
     */
    private List<String> responseDataElements = new ArrayList<String>();

}
