package com.sbroussi.dto.catalog;

import com.sbroussi.dto.annotations.DtoComment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Common shored properties of 'DtoRequestBean' and 'DtoResponseBean'.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "classname")
public abstract class DtoBean implements Comparable<DtoBean> {

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
     * The name of the java class.
     */
    private String classname;

    /**
     * The java class of the DTO class annotated with '@DtoRequest'.
     */
    private Class<?> dtoClass;


    // ---------------- from interface 'Comparable', to sort elements alphabetically in TreeSet
    public int compareTo(final DtoBean o) {
        // sort by name
        int comp = name.compareTo(o.name);
        if (comp == 0) {
            // and by classname for duplicates
            comp = classname.compareTo(o.classname);
        }
        return comp;
    }

}
