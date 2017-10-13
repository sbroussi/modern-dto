package com.sbroussi.soa.audit;

import com.sbroussi.soa.SoaDtoRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Simple slf4j logger to dump the request in the LOG file with INFO level.
 */
@Slf4j
@Getter
@Setter
public class AuditorLogger implements Auditor {

    /**
     * The maximum length of messages to log; if exceeds, the message is truncated.
     * This helps to reduce the size of log files for this 'default' Auditor.
     * <p>
     * Defaults to 2000 characters (will not truncate for values less than 100).
     * <p>
     * If truncated, the text '...[truncated]...' and the last 40 characters appears at the end of the log.
     */
    private int truncateSize = 2000;

    @Override
    public void traceBeforeRequest(final SoaDtoRequest request, final Map<String, Object> data) {
        if (log.isInfoEnabled()) {
            String rawMessage = request.getRawRequest();
            if (rawMessage == null) {
                rawMessage = "";
            }

            log.info("Send request [" + request.getDtoRequestAnnotation().name()
                    + "] (" + rawMessage.length() + " chars): [" + truncate(rawMessage) + "]");
        }

    }


    @Override
    public void traceAfterRequest(final SoaDtoRequest request, final Map<String, Object> data) {
        if (log.isInfoEnabled()) {
            String rawMessage = request.getRawResponse();
            if (rawMessage == null) {
                rawMessage = "";
            }

            log.info("Received response of request [" + request.getDtoRequestAnnotation().name()
                    + "] received (" + rawMessage.length() + " chars): [" + truncate(rawMessage) + "]");
        }
    }

    @Override
    public void traceOnTransportError(final SoaDtoRequest request, final Map<String, Object> data, final Throwable cause) {
        log.error("ERROR while sending request [" + request.getDtoRequestAnnotation().name() + "]: " + cause.getMessage());
    }


    @Override
    public void traceAfterResponseParsing(final SoaDtoRequest request, final Map<String, Object> data) {
        // nothing to do
    }

    @Override
    public void traceClose(final SoaDtoRequest request, final Map<String, Object> data, final Throwable cause) {
        // nothing to do
    }

    private String truncate(final String input) {
        final int len = input.length();
        // truncate ? (will not truncate for values less than 100)
        if ((truncateSize < 100) || (len <= truncateSize)) {
            return input;
        }
        final int keepLastChars = 40;
        return input.substring(0, truncateSize - keepLastChars) + "...[truncated]..."
                + input.substring(len - keepLastChars);
    }

}
