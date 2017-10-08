package com.sbroussi.soa.dto;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoFieldReference;

public class Datatypes {

    @DtoField(length = 1)
    public static class MY_DEFINITION_1 {
    }

    @DtoFieldReference(reference = MY_DEFINITION_3.class)
    public static class MY_DEFINITION_2 {
    }


    @DtoField(length = 8)
    public static class MY_DEFINITION_3 {
    }

}
