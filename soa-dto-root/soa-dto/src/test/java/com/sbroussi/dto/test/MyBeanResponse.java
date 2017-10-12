package com.sbroussi.dto.test;

import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.dto.annotations.DtoResponse;

@DtoResponse(name = "MY_RESP")
public class MyBeanResponse {

    @DtoFieldReference(reference = TestDatatypes.MY_DEFINITION_2.class)
    public String myField;
}
