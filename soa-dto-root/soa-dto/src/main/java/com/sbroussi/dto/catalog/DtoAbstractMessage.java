package com.sbroussi.dto.catalog;

import com.sbroussi.dto.annotations.DtoComment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * Common shored properties of 'DtoRequestBean' and 'DtoResponseBean'.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "dtoClassname")
public abstract class DtoAbstractMessage implements Comparable<DtoAbstractMessage> {

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
     * The short name of the request.
     */
    private String name;


    /**
     * The name of the java class of the DTO.
     */
    private String dtoClassname;

    /**
     * The java class of the DTO class annotated with '@DtoRequest'.
     */
    private Class<?> dtoClass;

    /**
     * The list of fields of the DTO.
     */
    private Set<DtoFieldBean> fields;

    // ---------------- from interface 'Comparable', to sort elements alphabetically in TreeSet

    public int compareTo(final DtoAbstractMessage o) {
        // sort by name
        int comp = name.compareTo(o.name);
        if (comp == 0) {
            // and by classname for duplicates
            comp = dtoClassname.compareTo(o.dtoClassname);
        }
        return comp;
    }

}
