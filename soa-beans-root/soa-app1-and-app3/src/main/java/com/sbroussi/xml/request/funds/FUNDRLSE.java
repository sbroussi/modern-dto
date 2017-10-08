package com.sbroussi.xml.request.funds;

import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.xml.request.v1_0.Datatypes;

public class FUNDRLSE {

    @DtoFieldReference(reference = Datatypes.MY_DEFINITION_2.class)
    public String fundId;

}
