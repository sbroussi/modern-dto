package com.sbroussi.soa.zos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MockZosRequest {

    private String channel;
    private String userId;
    private String userProfile;
    private String personNumber;
    private String sessionId;
    private String computerName;
    private String requestName;
    private boolean isUnsafe = false;

    public String formatHeader(final String data) {

        //SoaDtoRequest request = SoaDtoRequest.
        return null;
    }
}
