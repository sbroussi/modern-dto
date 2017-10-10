package com.sbroussi.dto.jms.test;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DtoRequest(name = "TEST_REQ",
        usedByApplications = {"app-test"},
        expectedResponses = {TestResponse.class}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequest {

    @DtoField(length = 10)
    public String myRequestField;

}

