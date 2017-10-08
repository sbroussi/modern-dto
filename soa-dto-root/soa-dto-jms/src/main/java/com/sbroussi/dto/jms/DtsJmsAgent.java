package com.sbroussi.dto.jms;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoFormatter;
import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.annotations.DtoRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DtsJmsAgent {

    public static String send(final DtoJmsContext jmsContext, final DtoJmsRequest request) {


        String applicationId = jmsContext.getApplicationId();
        DtoContext dtoContext = jmsContext.getDtoContext();
        DtoFormatter formatter = dtoContext.getDtoFormatter();

        // read DTO
        Object dto = request.getRequestDto();
        if (dto == null) {
            throw new IllegalArgumentException("property 'requestDto' of DtoJmsRequest is null");

        }
        final String dtoClassname = dto.getClass().getName();

        // read DtoRequest annotation
        final DtoRequest annotation = dto.getClass().getAnnotation(DtoRequest.class);
        request.setDtoRequestAnnotation(annotation);
        if (annotation == null) {
            throw new IllegalStateException("cannot send request; dto class ["
                    + dtoClassname + "] has no '@DtoRequest' annotation");
        }
        if (!DtoUtils.contains(annotation.usedByApplications(), applicationId)) {
            throw new IllegalStateException("cannot send request; dto class ["
                    + dtoClassname
                    + "] does not define that this applicationId [" + applicationId
                    + "] is allowed to call this request."
                    + " Please update the '@DtoRequest.usedByApplications' annotation");

        }

        // format JMS test message
        String rawJms = formatter.format(dto);
        request.setRawMessage(rawJms);


        if (log.isDebugEnabled()) {
            log.debug("send JMS [" + annotation.name() + "] [" + rawJms + "]");
        }

        final List<DtoJmsAudit> dtoJmsAuditors = jmsContext.getDtoJmsAuditors();
        if (dtoJmsAuditors != null) {
            for (final DtoJmsAudit auditor : dtoJmsAuditors) {
                auditor.traceRequest(jmsContext, request);
            }
        }
        return rawJms;

    }

}
