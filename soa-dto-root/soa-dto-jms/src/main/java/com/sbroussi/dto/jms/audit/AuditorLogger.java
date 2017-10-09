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
     * The maximum length of messages to log; if exceeds, the message is truncated.
     * This helps to reduce the size of log files for this 'default' Auditor.
     * <p>
     * Defaults to 2000 characters (will not truncate for values less than 100).
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
                    + "] (" + rawMessage.length() + " chars): [" + truncate(rawMessage) + "]");
        }

    }


    @Override
    public void traceAfterRequest(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        if (log.isInfoEnabled()) {
            String rawMessage = request.getRawResponse();
            if (rawMessage == null) {
                rawMessage = "";
            }

            log.info("Received response of JMS request [" + request.getDtoRequestAnnotation().name()
                    + "] received (" + rawMessage.length() + " chars): [" + truncate(rawMessage) + "]");
        }
    }


    @Override
    public void traceAfterResponseParsing(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
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
