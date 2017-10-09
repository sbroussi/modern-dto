package com.sbroussi.dto.jms;

import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.dto.annotations.DtoResponse;
import com.sbroussi.dto.jms.audit.Auditor;
import com.sbroussi.dto.jms.dialect.Dialect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        prepareExpectedResponses(request);


        // format the raw JMS text message
        final Dialect dialect = jmsContext.getDialect();

        dialect.formatToJmsText(jmsContext, request);

        final String rawJms = request.getRawRequest();


        // notify all auditors (before sending the JMS request)
        final List<Auditor> dtoJmsAuditors = jmsContext.getDtoJmsAuditors();
        if (dtoJmsAuditors != null) {
            for (final Auditor auditor : dtoJmsAuditors) {
                auditor.traceBeforeRequest(jmsContext, request);
            }
        }


        final String jmsName = annotation.name();
        try {
            // send JMS request and read response (if any)
            jmsContext.getMessageSender().sendMessage(jmsContext, request);

        } catch (Throwable t) {
            throw new JmsException("Error while sending JMS request [" + jmsName
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

    // -------------------------- cache of expected responses and errors

    /**
     * Internal cache of expected responses per request.
     * <p>
     * key: the name of the class of the '@DtoRequest'
     * value: a Map of all expected responses:
     * <p>
     * <p>
     * - key: String short name of the response ('@DtoResponse(name="ERROR")'
     * - value: The class of the response Dto annotated with '@DtoResponse'
     */
    private static Map<String, Map<String, Class>> expectedResponsesCache = new HashMap<String, Map<String, Class>>();

    /**
     * Read the map of '@DtoResponse' classes of the expected responses and errors,
     *
     * @param request the DTO request. This method will set the internal property 'expectedResponsesMap'
     */
    private static void prepareExpectedResponses(final DtoJmsRequest request) {

        final String key = request.getRequestDto().getClass().getName();
        Map<String, Class> responsesMap = expectedResponsesCache.get(key);
        if (responsesMap == null) {

            // prepare the list of all the responses and errors expected for this request
            responsesMap = new HashMap<String, Class>();

            DtoRequest annotation = request.getDtoRequestAnnotation();
            prepareExpectedResponses(responsesMap, annotation.expectedResponses());
            prepareExpectedResponses(responsesMap, annotation.technicalResponses());

            expectedResponsesCache.put(key, responsesMap);
        }
        request.setExpectedResponsesMap(responsesMap);
    }

    private static void prepareExpectedResponses(final Map<String, Class> responsesMap,
                                                 final Class[] expectedResponses) {
        if ((expectedResponses == null) || (expectedResponses.length == 0)) {
            return;
        }
        for (final Class<?> responseClass : expectedResponses) {
            final DtoResponse responseAnnotation = responseClass.getAnnotation(DtoResponse.class);
            final DtoResponse annotation = responseAnnotation;
            if (annotation == null) {
                throw new IllegalStateException("cannot send request; expected response class [" +
                        responseClass + "] has not annotation '@DtoResponse'");
            }

            final String name = annotation.name().trim();
            if (name.length() == 0) {
                throw new IllegalStateException("cannot send request; expected response class [" +
                        responseClass + "] defines an empty 'name' in annotation '@DtoResponse'");
            }

            // check for duplicate
            if (responsesMap.containsKey(name)) {
                throw new IllegalStateException("cannot send request;"
                        + "found 2 responses classes specifying the same name [" + name
                        + "] in their @DtoResponse annotation:"
                        + " class 1 [" + responseClass + "] and"
                        + " class 2 [" + responsesMap.get(name).getName() + "]");

            }

            responsesMap.put(name, responseClass);
        }
    }

    // --------------------------

}

