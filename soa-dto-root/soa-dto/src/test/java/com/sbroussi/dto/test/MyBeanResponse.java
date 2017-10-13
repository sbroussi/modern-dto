package com.sbroussi.dto.test;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.dto.annotations.DtoResponse;

@DtoComment(notes = {"this is a comment line 1 or 'MyBeanResponse'"})
@DtoResponse(name = "MY_RESP")
public class MyBeanResponse {

    @DtoFieldReference(reference = TestDatatypes.MY_DEFINITION_2.class)
    public String myField;
}
