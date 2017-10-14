package com.sbroussi.xml.request.v1_0;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoFieldReference;
import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.soa.dto.Apps;
import com.sbroussi.soa.dto.Datatypes;
import com.sbroussi.xml.response.v1_0.ADRVIRTUResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@DtoComment(notes = {"Retrieve the list of contacts",
        "",
        "- Emails ",
        "- Phone",
        "- Address"})
@DtoRequest(name = "ADRVIRTU",
        usedByApplications = {Apps.app1, Apps.app2, Apps.app3},
        expectedResponses = {ADRVIRTUResponse.class}
)
@Getter
@Setter
@Builder
public class ADRVIRTU {

    @DtoComment(notes = {"The type of contact",
            "",
            "- EMAIL: Emails ",
            "- PHONE: Phone",
            "- STREET: Address"})
    @DtoField(length = 5)
    public String typeOfContact;

    @DtoFieldReference(Datatypes.MY_DEFINITION_2.class)
    public String myRequestField1;

}
