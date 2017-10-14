package com.sbroussi.dto.catalog;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoFieldNumber;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * When a DTO field references another DataTypes to share/centralize definitions.
 * <p>
 * See @DtoFieldReference and @DtoFieldNumberReference.
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "name")
public class DtoDatatypeBean implements Comparable<DtoDatatypeBean> {

    private DtoField dtoField;
    private DtoFieldNumber dtoFieldNumber;

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
     * The name of the Datatype reference (the classname).
     */
    private String name;

    /**
     * The class defining the DataType reference.
     */
    private Class<?> referenceClass;

    /**
     * List of the DtoFieldBean that are referencing this DataType.
     */
    @Builder.Default
    private Set<DtoFieldBean> fields = new TreeSet<DtoFieldBean>();


    /**
     * Factory constructor.
     *
     * @param clazz the class of the DtoDatatypeBean.
     */
    public static DtoDatatypeBean fromClass(final Class<?> clazz) {

        final String className = clazz.getName();

        DtoField dtoField = clazz.getAnnotation(DtoField.class);
        DtoFieldNumber dtoFieldNumber = clazz.getAnnotation(DtoFieldNumber.class);

        final DtoComment dtoComment = clazz.getAnnotation(DtoComment.class);
        final List<String> dtoComments = DtoCatalogExtended.extractDtoComment(dtoComment, "Datatype " + className);

        final DtoDatatypeBean bean = DtoDatatypeBean.builder()
                .name(className)
                .referenceClass(clazz)
                .dtoField(dtoField)
                .dtoFieldNumber(dtoFieldNumber)
                .dtoComment(dtoComment)
                .dtoComments(dtoComments)
                .firstDtoComment(dtoComments.get(0))
                .build();

        return bean;

    }


    // ---------------- from interface 'Comparable', to sort elements alphabetically in TreeSet

    public int compareTo(final DtoDatatypeBean o) {
        return name.compareTo(o.name);
    }
}
