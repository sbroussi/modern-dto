package com.sbroussi.dto.jms;

import lombok.Getter;
import lombok.Setter;

/**
 * This Java Bean contains all the JMS response information.
 */
@Getter
@Setter
public class DtoJmsResponse {

    /**
     * When the JMS response was received and decoded.
     */
    private long timestampDecoded;


}
