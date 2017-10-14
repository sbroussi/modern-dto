package com.sbroussi.dto.catalog;

import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.annotations.DtoComment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An 'extended' catalog to retrieve more information on DTOs for Catalog generation or DEV tooling,
 * <p>
 * Is is recommended to keep one instance of this class in your application,
 * <p>
 * Note: This is NOT required to run standard applications.
 * The 'scan' method is implemented to help 'catalog generation', 'website generation'
 * or 'development' tooling (DEBUGREQ).
 */
@Getter
@Setter
public class DtoCatalogExtended {

    private final DtoCatalog dtoCatalog;

    /**
     * The map of 'DtoRequestBean' classes (for Catalog generation or DEV tooling),
     * <p>
     * - key: class name of the request.
     * <p>
     * - value: The bean collecting all useful information.
     */
    private Map<String, DtoRequestBean> requestBeans = new ConcurrentHashMap<String, DtoRequestBean>();


    /**
     * The map of 'DtoResponseBean' classes (for Catalog generation or DEV tooling),
     * <p>
     * - key: class name of the response.
     * <p>
     * - value: The bean collecting all useful information.
     */
    private Map<String, DtoResponseBean> responseBeans = new ConcurrentHashMap<String, DtoResponseBean>();


    /**
     * The list of Java packages.
     */
    private Set<String> packages = new TreeSet<String>();

    /**
     * For each Response, the list of Request that expect this response.
     * <p>
     * - key: class name of the response.
     * <p>
     * - value: The list of requests expecting this response.
     */
    private Map<String, Set<DtoRequestBean>> requestsByExpectedResponse = new ConcurrentHashMap<String, Set<DtoRequestBean>>();


    /**
     * The list of 'applicationId's referenced by '@DtoRequest'
     * <p>
     * - key: the ID of the application
     * <p>
     * - value: List of the DtoRequestBean that are allowed by this application.
     */
    private Map<String, Set<DtoRequestBean>> requestsByApplicationId = new ConcurrentHashMap<String, Set<DtoRequestBean>>();


    /**
     * Constructor (no DTOS are scanned, you must call the method 'scan' to analyse the DTOs).
     *
     * @param dtoCatalog the DTO Catalog.
     */
    public DtoCatalogExtended(final DtoCatalog dtoCatalog) {
        this.dtoCatalog = dtoCatalog;
    }

    /**
     * Reload all the internal Maps based on the current content of the 'dtoCatalog'.
     * <p>
     * You should call this method when all the DTO classes have been scanned.
     */
    public void scan() {

        // requests
        for (final Class<?> clazz : dtoCatalog.getRequests().values()) {

            // maintain the list of packages
            packages.add(clazz.getPackage().getName());

            final DtoRequestBean requestBean = getDtoRequestBean(clazz);

            // maintain the list of application IDs
            final String[] applicationIds = requestBean.getUsedByApplications();
            for (final String applicationId : applicationIds) {
                Set<DtoRequestBean> apps = requestsByApplicationId.get(applicationId);
                if (apps == null) {
                    apps = new TreeSet<DtoRequestBean>();
                    requestsByApplicationId.put(applicationId, apps);
                }
                apps.add(requestBean);
            }

            final Class[] expectedResponses = requestBean.getExpectedResponses();
            final List<DtoResponseBean> responseBeans = new ArrayList<DtoResponseBean>((expectedResponses == null) ? 0 : expectedResponses.length);
            requestBean.setExpectedResponseBeans(responseBeans);
            for (final Class expectedResponseClass : expectedResponses) {
                responseBeans.add(getDtoResponseBean(expectedResponseClass));


                // maintain the list of requests expecting this response
                String responseClassname = expectedResponseClass.getName();
                Set<DtoRequestBean> requestsExpectingThisResponse = requestsByExpectedResponse.get(responseClassname);
                if (requestsExpectingThisResponse == null) {
                    requestsExpectingThisResponse = new TreeSet<DtoRequestBean>();
                    requestsByExpectedResponse.put(responseClassname, requestsExpectingThisResponse);
                }
                requestsExpectingThisResponse.add(requestBean);
            }
        }

        // responses
        for (final Class<?> clazz : dtoCatalog.getResponses().values()) {

            // maintain the list of packages
            packages.add(clazz.getPackage().getName());

            final DtoResponseBean responseBean = getDtoResponseBean(clazz);

            Set<DtoRequestBean> requestsExpectingThisResponse = requestsByExpectedResponse.get(clazz.getName());

            responseBean.setRequestsExpectingThisResponse((requestsExpectingThisResponse == null)
                    ? new ArrayList<DtoRequestBean>(0)
                    : requestsExpectingThisResponse);
        }
    }


    /**
     * @param clazz the class of the DTO to scan
     * @return A bean that represents a '@DtoRequest' for Catalog generation or DEV tooling,
     */

    public DtoRequestBean getDtoRequestBean(final Class<?> clazz) {
        final String className = clazz.getName();
        DtoRequestBean bean = requestBeans.get(className);
        if (bean == null) {

            dtoCatalog.scanDtoRequest(clazz); // should have been done before

            bean = DtoRequestBean.fromClass(clazz);

            requestBeans.put(className, bean);
        }
        return bean;
    }

    /**
     * @param clazz the class of the DTO to scan
     * @return A bean that represents a '@DtoResponse' for Catalog generation or DEV tooling,
     */
    public DtoResponseBean getDtoResponseBean(final Class<?> clazz) {
        final String className = clazz.getName();
        DtoResponseBean bean = responseBeans.get(className);
        if (bean == null) {

            dtoCatalog.scanDtoResponse(clazz); // should have been done before

            bean = DtoResponseBean.fromClass(clazz);

            responseBeans.put(className, bean);
        }
        return bean;
    }


    /**
     * Extract DTO Comments and create a non-empty list of String (per line).
     *
     * @param dtoComment     the Annotation, may be null
     * @param defaultComment the default comment if no comment is defined
     * @return the list of comments
     */
    public static List<String> extractDtoComment(final DtoComment dtoComment, final String defaultComment) {
        List<String> comments = new ArrayList<String>();
        if (dtoComment != null) {
            final String[] notes = dtoComment.notes();
            if (notes != null) {
                for (final String line : notes) {
                    // the first line cannot be empty: it is the 'default' comment of the DTO
                    if (comments.isEmpty() && line.trim().length() == 0) {
                        continue;
                    }
                    comments.add(line);
                }
            }

        }
        if (comments.isEmpty()) {
            comments.add(defaultComment);
        }
        return comments;
    }
}
