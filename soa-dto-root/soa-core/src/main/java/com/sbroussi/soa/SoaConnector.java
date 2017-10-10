package com.sbroussi.soa;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.dto.transport.TransportException;
import com.sbroussi.soa.audit.Auditor;
import com.sbroussi.soa.dialect.Dialect;

import java.util.List;

/**
 * This class will send the request and read responses.
 */
public class SoaConnector {

    /**
     * Send the request and read responses (delegate the PUT to 'soaContext.messageSender').
     *
     * @param soaContext the current SOA context
     * @param request    The DTO request wrapper. The responses are populated in this bean.
     */
    public static void send(final SoaContext soaContext, final SoaDtoRequest request) {

        final String applicationId = soaContext.getApplicationId();

        // read DTO
        final Object dto = request.getRequestDto();
        if (dto == null) {
            throw new IllegalArgumentException("property 'requestDto' of SoaDtoRequest is null");

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
        DtoContext dtoContext = soaContext.getDtoContext();
        dtoContext.getDtoCatalog().scanDtoRequest(request.getRequestDto().getClass());

        // format the raw text message of the request
        final Dialect dialect = soaContext.getDialect();
        dialect.formatToRequestMessage(soaContext, request);

        // notify all auditors (before sending the request)
        final List<Auditor> auditors = soaContext.getAuditors();
        if (auditors != null) {
            for (final Auditor auditor : auditors) {
                auditor.traceBeforeRequest(soaContext, request);
            }
        }


        final String requestName = annotation.name();
        final String rawRequest = request.getRawRequest();
        try {
            // send request and read response (if any)


            // remember the 'send' time
            final long start = System.currentTimeMillis();
            request.setTimestampSend(start);

            if (request.isOneWayRequest()) {

                soaContext.getMessageSender().send(rawRequest);

            } else {

                final String rawResponse = soaContext.getMessageSender()
                        .sendAndReceive(rawRequest, request.getReplyTimeoutInMs());

                request.setRawResponse(rawResponse);
            }

        } catch (Throwable t) {
            throw new TransportException("Error while sending request [" + requestName
                    + "] with message [" + rawRequest + "]", t);
        }


        // notify all auditors (after receiving the response)
        if (auditors != null) {
            for (final Auditor auditor : auditors) {
                auditor.traceAfterRequest(soaContext, request);
            }
        }

        // response expected ?
        if (!request.isOneWayRequest()) {

            // parse the raw text message of the response
            dialect.parseFromResponseMessage(soaContext, request);

            // notify all auditors (after parsing the response)
            if (auditors != null) {
                for (final Auditor auditor : auditors) {
                    auditor.traceAfterResponseParsing(soaContext, request);
                }
            }
        }

    }


}

