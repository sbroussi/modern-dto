package com.sbroussi.dto.jms;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoFormatter;
import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.annotations.DtoRequest;

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
        final DtoContext dtoContext = jmsContext.getDtoContext();
        final DtoFormatter formatter = dtoContext.getDtoFormatter();

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

        // format the raw JMS text message
        final String rawJms = formatter.format(dto);
        request.setRawRequest(rawJms);

        final String jmsName = annotation.name();

        // notify all auditors (before sending the JMS request)
        final List<DtoJmsAudit> dtoJmsAuditors = jmsContext.getDtoJmsAuditors();
        if (dtoJmsAuditors != null) {
            for (final DtoJmsAudit auditor : dtoJmsAuditors) {
                auditor.traceBeforeRequest(jmsContext, request);
            }
        }

        try {
            // send the JMS message and read responses
            jmsContext.getMessageSender().sendMessage(jmsContext, request);

        } catch (Throwable t) {

            throw new JmsException("Error while sending JMS request [" + jmsName
                    + "] with message [" + rawJms + "]", t);
        }

        // notify all auditors (after receiving the JMS responses)
        if (dtoJmsAuditors != null) {
            for (final DtoJmsAudit auditor : dtoJmsAuditors) {
                auditor.traceAfterResponse(jmsContext, request);
            }
        }

    }

}
