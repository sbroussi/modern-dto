package com.sbroussi.dto.test;


import com.sbroussi.dto.annotations.DtoFieldReference;

public class MyBean {


    @DtoFieldReference(reference = TestDatatypes.MY_DEFINITION_2.class)
    public String myField;


}
