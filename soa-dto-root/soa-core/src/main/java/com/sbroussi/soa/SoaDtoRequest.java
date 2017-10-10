package com.sbroussi.soa;


import com.sbroussi.dto.annotations.DtoRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
public class SoaDtoRequest {

    /**
     * The request DTO,
     */
    private final Object requestDto;

    /**
     * The '@DtoRequest' annotation of the request DTO.
     */
    private DtoRequest dtoRequestAnnotation;


    /**
     * Indicates if this request is a ONE-WAY request (fire and forget).
     * Set it to FALSE if this request expects a response (default).
     * Set it to TRUE if this request does NOT expect a response (one-way pattern).
     */
    private boolean oneWayRequest = false;


    /**
     * The time to wait for a reply message; 40 seconds by default.
     */
    private long replyTimeoutInMs = 40L * 60L * 1000L;


    /**
     * The user identifier.
     */
    private String userId;

    /**
     * The profile of the user on the back-end.
     */
    private String userProfile;

    /**
     * The user's computer name.
     */
    private String userComputerName;

    /**
     * The locale of the user (may be used to build i18n error messages).
     */
    private Locale userLocale;

    /**
     * Security: Fraud detection: TRUE to indicate that the user sessions seems to be 'dangerous' (a phishing attack ?).
     */
    private boolean isUnsafe = false;

    /**
     * The internal person number of the logged in user.
     * <p>
     * This is useful for business operations where the 'userId' is not the internal ID of the customer.
     */
    private String personNumber;

    /**
     * For stateful back-end systems that maintain a user session.
     */
    private String sessionId;

    // ----------------- technical properties updated during Transport
    /**
     * INTERNAL: the RAW formatted message sent as request.
     */
    private String rawRequest;

    /**
     * INTERNAL: the timestamp when the request was sent.
     */
    private long timestampSend;


    /**
     * INTERNAL: the RAW message received as response.
     */
    private String rawResponse;


    /**
     * The response object (the real implementation is defined by the 'Dialect'),
     */
    private SoaDtoResponse soaDtoResponse;


    public SoaDtoRequest(final Object requestDto) {
        this.requestDto = requestDto;
    }


}
