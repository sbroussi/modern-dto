package com.sbroussi.dto.jms;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple slf4j logger to dump the JMS request in the LOG file with INFO level.
 */
@Slf4j
public class DtoJmsAuditLogger implements DtoJmsAudit {

    @Override
    public void traceRequest(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        if (log.isInfoEnabled()) {
            log.info("send JMS request [" + request.getDtoRequestAnnotation().name() + "] ["
                    + request.getRawMessage() + "]");
        }

    }
}
