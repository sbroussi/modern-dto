package com.sbroussi.xml.request.funds;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.soa.dto.Apps;
import com.sbroussi.soa.dto.Datatypes;

@DtoComment(notes = {"The 'FUNDRLSE' request"})
@DtoRequest(name = "FUNDRLSE",
        usedByApplications = {Apps.app1, Apps.app3},
        expectedResponses = {})
public class FUNDRLSE {

    @DtoFieldReference(Datatypes.MY_DEFINITION_2.class)
    public String fundId;

}
