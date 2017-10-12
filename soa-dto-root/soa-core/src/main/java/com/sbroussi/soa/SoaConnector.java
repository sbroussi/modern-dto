package com.sbroussi.soa;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.dto.transport.MessageSender;
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
     * @param request The DTO request wrapper. The responses are populated in this bean.
     */
    public static void send(final SoaDtoRequest request) {

        final SoaContext soaContext = request.getSoaContext();
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

        // clear response (if sent twice)
        request.setSoaDtoResponse(null);

        // read the map of '@DtoResponse' classes of the expected responses and errors,
        DtoContext dtoContext = soaContext.getDtoContext();
        dtoContext.getDtoCatalog().scanDtoRequest(request.getRequestDto().getClass());

        // format the raw text message of the request
        final Dialect dialect = soaContext.getDialect();
        dialect.formatToRequestMessage(request);

        // notify all auditors (before sending the request)
        final List<Auditor> auditors = soaContext.getAuditors();
        if (auditors != null) {
            for (final Auditor auditor : auditors) {
                auditor.traceBeforeRequest(request);
            }
        }


        final String requestName = annotation.name();
        final String rawRequest = request.getRawRequest();

        // remember the 'send' time
        final long start = System.currentTimeMillis();
        request.setTimestampSend(start);

        try {
            // send request and read response (if any)

            final MessageSender messageSender = soaContext.getMessageSender();
            if (messageSender == null) {
                throw new IllegalArgumentException("no 'MessageSender' set in 'SoaContext'");
            }
            if (request.isOneWayRequest()) {

                messageSender.send(rawRequest);

            } else {

                final String rawResponse = messageSender
                        .sendAndReceive(rawRequest, request.getReceiveTimeout());

                request.setRawResponse(rawResponse);
            }


        } catch (Throwable t) {

            throw new TransportException("Error while sending request [" + requestName
                    + "] with message [" + rawRequest + "]", t);

        } finally {

            // compute call duration
            request.setDuration(System.currentTimeMillis() - start);

        }


        // notify all auditors (after receiving the response)
        if (auditors != null) {
            for (final Auditor auditor : auditors) {
                auditor.traceAfterRequest(request);
            }
        }

        // response expected ?
        if (!request.isOneWayRequest()) {

            // parse the raw text message of the response
            dialect.parseFromResponseMessage(request);

            // notify all auditors (after parsing the response)
            if (auditors != null) {
                for (final Auditor auditor : auditors) {
                    auditor.traceAfterResponseParsing(request);
                }
            }
        }

    }


}

