package com.sbroussi.dto.jms.audit;

import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple slf4j logger to dump the JMS request in the LOG file with INFO level.
 */
@Slf4j
@Getter
@Setter
public class AuditorLogger implements Auditor {

    /**
     * The size of the message to truncate (will not truncate for values less than 100).
     * <p>
     * Defaults to 2000 characters.
     * <p>
     * If truncated, the text '...[truncated]...' and the last 40 characters appears at the end of the log.
     */
    private int truncateSize = 2000;

    @Override
    public void traceBeforeRequest(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        if (log.isInfoEnabled()) {
            String rawMessage = request.getRawRequest();
            if (rawMessage == null) {
                rawMessage = "";
            }

            log.info("Send JMS request [" + request.getDtoRequestAnnotation().name()
                    + "] (" + rawMessage.length() + " chars): [" + rawMessage + "]");
        }

    }

    @Override
    public void traceAfterResponse(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        if (log.isInfoEnabled()) {
            String rawMessage = request.getRawResponse();
            if (rawMessage == null) {
                rawMessage = "";
            }

            // truncate 2 KB
            final int len = rawMessage.length();
            final String msg;
            if ((truncateSize < 100) || (len <= truncateSize)) {
                msg = rawMessage;
            } else {
                final int keepLastChars = 40;
                msg = rawMessage.substring(0, truncateSize - keepLastChars) + "...[truncated]..."
                        + rawMessage.substring(len - keepLastChars);
            }

            log.info("Received response of JMS request [" + request.getDtoRequestAnnotation().name()
                    + "] received (" + len + " chars): [" + msg + "]");
        }
    }
}
