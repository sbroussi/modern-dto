package com.sbroussi.dto.catalog;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

/**
 * A bean that represents a '@DtoResponse' for Catalog generation or DEV tooling,
 * <p>
 * This bean collect and expose all useful information in one POJO.
 */
@Getter
@Setter
@Builder
public class DtoResponseBean extends DtoBean {

    /**
     * The annotation.
     */
    private DtoResponse dtoResponse;

    /**
     * The list of requests expecting this response.
     */
    private Collection<DtoRequestBean> requestsExpectingThisResponse;

    /**
     * Factory constructor.
     *
     * @param clazz the class of the DtoRequest.
     */
    public static DtoResponseBean fromClass(final Class<?> clazz) {
        final DtoResponse dtoResponse = clazz.getAnnotation(DtoResponse.class);
        final DtoComment dtoComment = clazz.getAnnotation(DtoComment.class);

        final List<String> dtoComments = DtoCatalogExtended.extractDtoComment(dtoComment, "Response " + dtoResponse.name());

        final DtoResponseBean bean = DtoResponseBean.builder()
                .dtoResponse(dtoResponse)
                .build();

        // common fields
        bean.setName(dtoResponse.name());

        bean.setDtoComment(dtoComment);
        bean.setDtoComments(dtoComments);
        bean.setFirstDtoComment(dtoComments.get(0));
        bean.setDtoComments(dtoComments);
        bean.setClassname(clazz.getName());
        bean.setDtoClass(clazz);

        return bean;

    }

}
