package com.sbroussi.dto.jms;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.dto.jms.audit.Auditor;
import com.sbroussi.dto.jms.dialect.Dialect;
import com.sbroussi.dto.transport.TransportException;

import java.util.List;

/**
 * This class will send the JMS message and read responses.
 */
public class DtoJmsConnector {

    /**
     * Send the JMS message and read responses (delegate the PUT to 'jmsContext.messageSender').
     *
     * @param jmsContext the DTO JMS context
     * @param request    The DTO request wrapper. The responses are populated in this bean.
     */
    public static void send(final DtoJmsContext jmsContext, final DtoJmsRequest request) {

        final String applicationId = jmsContext.getApplicationId();

        // read DTO
        final Object dto = request.getRequestDto();
        if (dto == null) {
            throw new IllegalArgumentException("property 'requestDto' of DtoJmsRequest is null");

        }
        final String dtoClassname = dto.getClass().getName();

        // read DtoRequest annotation
        final DtoRequest annotation = dto.getClass().getAnnotation(DtoRequest.class);
        request.setDtoRequestAnnotation(annotation);
        if (annotation == null) {
            throw new IllegalArgumentException("cannot send request; dto class ["
                    + dtoClassname + "] has no '@DtoRequest' annotation");
        }

        if (!DtoUtils.contains(annotation.usedByApplications(), applicationId)) {
            throw new IllegalStateException("cannot send request; application [" + applicationId
                    + "] is NOT allowed to call the request [" + dtoClassname
                    + "] Please update the '@DtoRequest.usedByApplications' annotation");
        }
        if (annotation.name().trim().length() == 0) {
            throw new IllegalStateException("cannot send request; request class [" +
                    dtoClassname + "] defines an empty 'name' in annotation '@DtoRequest'");
        }

        // read the map of '@DtoResponse' classes of the expected responses and errors,
        DtoContext dtoContext = jmsContext.getDtoContext();
        dtoContext.getDtoCatalog().scanDtoRequest(request.getRequestDto().getClass());

        // format the raw JMS text message
        final Dialect dialect = jmsContext.getDialect();

        dialect.formatToJmsText(jmsContext, request);

        // notify all auditors (before sending the JMS request)
        final List<Auditor> dtoJmsAuditors = jmsContext.getDtoJmsAuditors();
        if (dtoJmsAuditors != null) {
            for (final Auditor auditor : dtoJmsAuditors) {
                auditor.traceBeforeRequest(jmsContext, request);
            }
        }


        final String jmsName = annotation.name();
        final String rawJms = request.getRawRequest();
        try {
            // send JMS request and read response (if any)


            // remember the 'send' time
            final long start = System.currentTimeMillis();
            request.setTimestampSend(start);

            if (request.isOneWayRequest()) {

                jmsContext.getMessageSender().send(rawJms);

            } else {

                final String rawResponse = jmsContext.getMessageSender()
                        .sendAndReceive(rawJms, request.getReplyTimeoutInMs());

                request.setRawResponse(rawResponse);
            }

        } catch (Throwable t) {
            throw new TransportException("Error while sending JMS request [" + jmsName
                    + "] with message [" + rawJms + "]", t);
        }


        // notify all auditors (after receiving the JMS response)
        if (dtoJmsAuditors != null) {
            for (final Auditor auditor : dtoJmsAuditors) {
                auditor.traceAfterRequest(jmsContext, request);
            }
        }

        // response expected ?
        if (!request.isOneWayRequest()) {

            // parse the raw JMS text message
            dialect.parseFromJmsText(jmsContext, request);

            // notify all auditors (after parsing the JMS response)
            if (dtoJmsAuditors != null) {
                for (final Auditor auditor : dtoJmsAuditors) {
                    auditor.traceAfterResponseParsing(jmsContext, request);
                }
            }
        }

    }


}

