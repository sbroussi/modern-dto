package com.sbroussi.soa.zos;

import lombok.Getter;
import lombok.Setter;

/**
 * Container for a z/OS response header.
 */
@Getter
@Setter
public class ZosResponseHeader {

    /**
     * The name of the header.
     */
    private String name = null;

    /**
     * The length of the header.
     */
    private int length = 0;

    /**
     * The flags of the header.
     */
    private String flags = null;

    /**
     * The User Host Index decoded from the response: X(4).
     */
    private String userHostIndex = null;

    /**
     * The user identifier.
     */
    private String userId = null;

    /**
     * The user profile.
     */
    private String userProfile = null;

    /**
     * The user session on the z/OS (FormING session).
     */
    private String sessionId = null;

    /**
     * The user's computer name.
     */
    private String userComputerName = null;


}
