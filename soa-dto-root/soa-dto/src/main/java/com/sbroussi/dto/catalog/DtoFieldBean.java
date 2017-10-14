package com.sbroussi.dto.catalog;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoFieldNumber;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "name")
public class DtoFieldBean implements Comparable<DtoFieldBean> {

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
     * The description of the parent bean (DtoRequestBean or DtoResponseBean).
     */
    private DtoAbstractMessage dtoBean;

    private Field field;

    private String name;

    private int length;

    /**
     * Optional: when a field references another DataTypes to
     * share/centralize definitions (see @DtoFieldReference and @DtoFieldNumberReference).
     */
    private String datatypeReference;

    /**
     * The type ('X' for String or '9' for Numbers),
     */
    private char type;

    private int minOccurs = 1;
    private int maxOccurs = 1;

    private int positionStart;
    private int positionEnd;


    // ---------------- from interface 'Comparable', to sort elements alphabetically in TreeSet

    public int compareTo(final DtoFieldBean o) {
        // sort by the short name of the parent DTO
        int comp = dtoBean.getName().compareTo(o.dtoBean.getName());
        if (comp == 0) {
            // sort by name
            comp = name.compareTo(o.name);
        }
        return comp;
    }

}
