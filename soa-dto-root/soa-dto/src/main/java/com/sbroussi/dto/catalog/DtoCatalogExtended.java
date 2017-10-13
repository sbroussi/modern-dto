package com.sbroussi.dto.catalog;

import com.sbroussi.dto.DtoCatalog;
import com.sbroussi.dto.annotations.DtoComment;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An 'extended' catalog to retrieve more information on DTOs for Catalog generation or DEV tooling,
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
     * The list of 'applicationId's referenced by '@DtoRequest'
     * <p>
     * - key: the ID of the application
     * <p>
     * - value: List of the '@DtoRequest' that are allowed by this application.
     */
    private Map<String, List<Class<?>>> requestsByApplicationId = new ConcurrentHashMap<String, List<Class<?>>>();


    /**
     * Constructor (no DTOS are scanned, you must call the method 'refresh' to analyse the DTOs).
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
    public void refresh() {

        for (final Class<?> clazz : dtoCatalog.getRequests().values()) {

            final DtoRequestBean requestBean = getDtoRequestBean(clazz);

            // maintain the list of application IDs
            final String[] applicationIds = requestBean.getUsedByApplications();
            for (final String applicationId : applicationIds) {
                List<Class<?>> apps = requestsByApplicationId.get(applicationId);
                if (apps == null) {
                    apps = new ArrayList<Class<?>>();
                    requestsByApplicationId.put(applicationId, apps);
                }
                if (!apps.contains(clazz)) {
                    apps.add(clazz);
                }
            }

            final Class[] expectedResponses = requestBean.getExpectedResponses();
            final List<DtoResponseBean> responseBeans = new ArrayList<DtoResponseBean>((expectedResponses == null) ? 0 : expectedResponses.length);
            requestBean.setExpectedResponseBeans(responseBeans);
            for (final Class expectedResponseClass : expectedResponses) {
                responseBeans.add(getDtoResponseBean(expectedResponseClass));
            }
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
