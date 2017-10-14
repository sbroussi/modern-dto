package com.sbroussi.dto.catalog;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * A bean that represents a '@DtoRequest' for Catalog generation or DEV tooling,
 * <p>
 * This bean collect and expose all useful information in one POJO.
 */
@Getter
@Setter
@Builder
public class DtoRequestBean extends DtoBean {

    /**
     * The annotation.
     */
    private DtoRequest dtoRequest;


    /**
     * The list of expected responses (as class).
     */
    private Class[] expectedResponses;


    /**
     * The list of expected responses (as DtoResponseBean).
     */
    private List<DtoResponseBean> expectedResponseBeans;

    /**
     * The list of IDs of applications that use this request.
     */
    private String[] usedByApplications;

    /**
     * /**
     * The list of standard ERROR responses or technical messages that may always be returned.
     */
    private Class[] technicalResponses;

    /**
     * Factory constructor.
     *
     * @param clazz the class of the DtoRequest.
     */
    public static DtoRequestBean fromClass(final Class<?> clazz) {
        final DtoRequest dtoRequest = clazz.getAnnotation(DtoRequest.class);
        final DtoComment dtoComment = clazz.getAnnotation(DtoComment.class);

        final List<String> dtoComments = DtoCatalogExtended.extractDtoComment(dtoComment, "Request " + dtoRequest.name());

        final DtoRequestBean bean = DtoRequestBean.builder()
                .dtoRequest(dtoRequest)
                .expectedResponses(dtoRequest.expectedResponses())
                .usedByApplications(dtoRequest.usedByApplications())
                .technicalResponses(dtoRequest.technicalResponses())
                .build();

        // common fields
        bean.setName(dtoRequest.name());

        bean.setDtoComment(dtoComment);
        bean.setDtoComments(dtoComments);
        bean.setFirstDtoComment(dtoComments.get(0));
        bean.setDtoComments(dtoComments);
        bean.setClassname(clazz.getName());
        bean.setDtoClass(clazz);

        return bean;

    }

}
