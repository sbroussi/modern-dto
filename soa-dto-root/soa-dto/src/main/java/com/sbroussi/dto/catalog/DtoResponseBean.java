package com.sbroussi.dto.catalog;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = "classname")
public class DtoResponseBean implements Comparable<DtoResponseBean> {

    /**
     * The annotation.
     */
    private DtoResponse dtoResponse;

    /**
     * The documentation annotation.
     */
    private DtoComment dtoComment;

    /**
     * The documentation content.
     */
    private String firstDtoComment;

    /**
     * The documentation content.
     */
    private List<String> dtoComments;


    /**
     * The short name of the response.
     */
    private String name;

    /**
     * The name of the java class.
     */
    private String classname;

    /**
     * The java class of the DTO class annotated with '@DtoResponse'.
     */
    private Class<?> dtoClass;

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
                .dtoComment(dtoComment)
                .dtoComments(dtoComments)
                .firstDtoComment(dtoComments.get(0))
                .dtoComments(dtoComments)
                .name(dtoResponse.name())
                .classname(clazz.getName())
                .dtoClass(clazz)
                .build();

        return bean;

    }

    // ---------------- from interface 'Comparable', to sort elements alphabetically in TreeSet
    @Override
    public int compareTo(final DtoResponseBean o) {
        // sort by name
        int comp = name.compareTo(o.name);
        if (comp == 0) {
            // and by classname for duplicates
            comp = classname.compareTo(o.classname);
        }
        return comp;
    }
}