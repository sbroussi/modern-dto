package com.sbroussi.xml.response.v1_0;

import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.dto.annotations.DtoResponse;
import com.sbroussi.soa.dto.Datatypes;

@DtoResponse(name = "ADRVIRTU")
public class ADRVIRTUResponse {

    @DtoFieldReference(reference = Datatypes.MY_DEFINITION_2.class)
    public String myResponseField1;

    @DtoFieldReference(reference = Datatypes.MY_DEFINITION_2.class)
    public String myResponseField2;

}
