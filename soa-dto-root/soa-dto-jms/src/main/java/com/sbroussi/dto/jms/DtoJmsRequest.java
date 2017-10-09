package com.sbroussi.dto.jms;


import com.sbroussi.dto.annotations.DtoRequest;
import lombok.Getter;
import lombok.Setter;

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
