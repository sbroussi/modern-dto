package com.sbroussi.xml.request.funds;

import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.soa.dto.Apps;
import com.sbroussi.soa.dto.Datatypes;

@DtoRequest(name = "ADRVIRTU",
        usedByApplications = {Apps.app1, Apps.app3},
        expectedResponses = {})
public class FUNDRLSE {

    @DtoFieldReference(reference = Datatypes.MY_DEFINITION_2.class)
    public String fundId;

}
