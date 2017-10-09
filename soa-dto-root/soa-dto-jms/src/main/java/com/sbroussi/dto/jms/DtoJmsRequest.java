package com.sbroussi.dto.jms;


import com.sbroussi.dto.annotations.DtoRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Getter
@Setter
public class DtoJmsRequest {

    /**
     * The request DTO,
     */
    private final Object requestDto;

    /**
     * The '@DtoRequest' annotation of the request DTO.
     */
    private DtoRequest dtoRequestAnnotation;


    /**
     * Indicates if this JMS request is a ONE-WAY request (fire and forget).
     * Set it to FALSE if this request expects a JMS response (default).
     * Set it to TRUE if this request does NOT expect a JMS response (one-way pattern).
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

    // ----------------- technical properties updated during JMS dialog
    /**
     * Process info: the formatted JMS message sent as request.
     */
    private String rawRequest;

    /**
     * Process info: the timestamp when the JMS request was sent.
     */
    private long timestampSend;

    /**
     * Process info: the messageId (correlation-ID) to send/receive JMS Request-Response synchronously,
     */
    private String correlationId;

    /**
     * Process info: the JMS message received as response.
     */
    private String rawResponse;


    public DtoJmsRequest(final Object requestDto) {
        this.requestDto = requestDto;
    }

}
