package com.sbroussi.dto;

import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This catalog maintains a map of all DTOs that are being used ('@DtoRequest' or '@DtoResponse').
 * <p>
 * Is is recommended to keep one instance of this class in your application,
 * <p>
 * Note: To have a full map of ALL available '@DtoRequest' or '@DtoResponse'
 * you can call the 'scan' method. But this is NOT required to run standard applications.
 * The 'scan' method is implemented to help 'catalog generation', 'website generation'
 * or 'development' tooling (DEBUGREQ).
 */
@Getter
public class DtoCatalog {

    /**
     * The map of '@DtoRequest' classes,
     * <p>
     * - key: class name of the request.
     * <p>
     * - value: The class of the Dto annotated with '@DtoRequest'
     */
    private Map<String, Class> requests = new ConcurrentHashMap<String, Class>();

    /**
     * The map of '@DtoResponse' classes,
     * <p>
     * - key: class name of the response.
     * <p>
     * - value: The class of the Dto annotated with '@DtoResponse'
     */
    private Map<String, Class> responses = new ConcurrentHashMap<String, Class>();

    /**
     * The map of '@DtoResponse' classes by their short name (for example JMS name 'ADRVIRTU').
     * <p>
     * - map value, key: String short JMS name of the response ('@DtoResponse(name="ERROR")'
     * <p>
     * - value: The class of the Dto annotated with '@DtoResponse'
     */
    private Map<String, Class> responsesByName = new ConcurrentHashMap<String, Class>();

    /**
     * The map of expected '@DtoResponse' classes for each '@DtoRequest'.
     * <p>
     * - key: The class of the request Dto annotated with '@DtoRequest'
     * <p>
     * - value: A Map of expected responses (name, class):
     * <p>
     * - map value, key: String short name of the response ('@DtoResponse(name="ERROR")'
     * <p>
     * - map value, value: The class of the response Dto annotated with '@DtoResponse'
     */
    private Map<Class, Map<String, Class>> expectedResponsesByRequest = new ConcurrentHashMap<Class, Map<String, Class>>();


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
     * Scan a DTO with annotations '@DtoRequest' or '@DtoResponse'.
     *
     * @param clazz the class of the DTO to scan
     */
    public void scanDtoRequest(final Class<?> clazz) {
        final String className = clazz.getName();
        if (requests.containsKey(className)) {
            // already scanned
            return;
        }

        final DtoRequest dtoRequest = clazz.getAnnotation(DtoRequest.class);
        if (dtoRequest == null) {
            throw new DtoException("Request class [" + className
                    + "] has not annotation '@DtoRequest'");
        }

        final String name = dtoRequest.name().trim();
        if (name.length() == 0) {
            throw new DtoException("Request class [" + className
                    + "] defines an empty 'name' in annotation '@DtoRequest'");
        }

        // prepare the list of all the responses and errors expected for this request

        // no need of 'multi-thread safe' map here (for read only access)
        Map<String, Class> responsesMap = new HashMap<String, Class>();
        prepareExpectedResponses(responsesMap, dtoRequest.expectedResponses());
        prepareExpectedResponses(responsesMap, dtoRequest.technicalResponses());

        expectedResponsesByRequest.put(clazz, responsesMap);

        requests.put(className, clazz);
    }

    /**
     * Scan a DTO with annotations '@DtoRequest' or '@DtoResponse'.
     *
     * @param clazz the class of the DTO to scan
     */
    public void scanDtoResponse(final Class<?> clazz) {
        final String className = clazz.getName();
        if (responses.containsKey(className)) {
            // already scanned
            return;
        }

        final DtoResponse dtoResponse = clazz.getAnnotation(DtoResponse.class);
        if (dtoResponse == null) {
            throw new DtoException("Response class [" + className
                    + "] has not annotation '@DtoResponse'");
        }

        final String name = dtoResponse.name().trim();
        if (name.length() == 0) {
            throw new DtoException("Response class [" + className
                    + "] defines an empty 'name' in annotation '@DtoResponse'");
        }

        // check for duplicate
        if (responsesByName.containsKey(name)) {
            throw new DtoException("Found 2 responses classes specifying the same name [" + name
                    + "] in their @DtoResponse annotation:"
                    + " class 1 [" + className + "] and"
                    + " class 2 [" + responsesByName.get(name).getName() + "]");

        }

        responsesByName.put(name, clazz);
        responses.put(className, clazz);
    }


    private void prepareExpectedResponses(final Map<String, Class> responsesMap,
                                          final Class[] expectedResponses) {
        if ((expectedResponses == null) || (expectedResponses.length == 0)) {
            return;
        }
        for (final Class<?> responseClass : expectedResponses) {

            scanDtoResponse(responseClass);

            final DtoResponse responseAnnotation = responseClass.getAnnotation(DtoResponse.class);
            responsesMap.put(responseAnnotation.name(), responseClass);
        }
    }

}
