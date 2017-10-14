package com.sbroussi.dto.test;


import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.dto.annotations.DtoRequest;

@DtoRequest(name = "MyBeanWithResponse",
        expectedResponses = {MyBeanResponse.class},
        usedByApplications = {"app-test-A", "app-test-B"})
public class MyBeanWithResponse {


    @DtoFieldReference(TestDatatypes.MY_DEFINITION_2.class)
    public String myField;


}
