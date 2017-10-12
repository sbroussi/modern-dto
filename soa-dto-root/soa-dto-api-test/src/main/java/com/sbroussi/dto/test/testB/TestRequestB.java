package com.sbroussi.dto.test.testB;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DtoRequest(name = "TESTREQB",
        usedByApplications = {"app-test"},
        expectedResponses = {TestResponseB.class}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequestB {

    @DtoField(length = 10)
    public String myRequestField;

}

