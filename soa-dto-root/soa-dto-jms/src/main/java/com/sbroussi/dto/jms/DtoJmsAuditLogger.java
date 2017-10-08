package com.sbroussi.dto.jms;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple slf4j logger to dump the JMS request in the LOG file with INFO level.
 */
@Slf4j
public class DtoJmsAuditLogger implements DtoJmsAudit {

    @Override
    public void traceBeforeRequest(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        if (log.isInfoEnabled()) {
            String rawMessage = request.getRawRequest();
            log.info("Send JMS request [" + request.getDtoRequestAnnotation().name()
                    + "] (" + rawMessage.length() + " chars): [" + rawMessage + "]");
        }

    }

    @Override
    public void traceAfterResponse(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        if (log.isInfoEnabled()) {
            String rawResponse = request.getRawResponse();
            if (rawResponse == null) {
                rawResponse = "";
            }

            // truncate 2 KB
            final int messageLength = (rawResponse == null) ? -1 : rawResponse.length();
            final String messageLog;
            if (messageLength <= 2000) {
                messageLog = rawResponse;
            } else {
                final int keepEndOfMessage = 40;
                messageLog = rawResponse.substring(0, 2000 - keepEndOfMessage) + "...[truncated]..."
                        + rawResponse.substring(messageLength - keepEndOfMessage);
            }


            log.info("Received response of JMS request [" + request.getDtoRequestAnnotation().name()
                    + "] received (" + messageLength + " chars): [" + messageLog + "]");
        }
    }
}
