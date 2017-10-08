package com.sbroussi.xml.request.v1_0;

import com.sbroussi.dto.annotations.DtoFieldReference;

public class ADRVIRTU {

    @DtoFieldReference(reference = Datatypes.MY_DEFINITION_2.class)
    public String myField;


}
