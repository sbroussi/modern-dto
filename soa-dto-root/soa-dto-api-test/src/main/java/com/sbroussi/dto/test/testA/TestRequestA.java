package com.sbroussi.dto.test.testA;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DtoComment(notes = {"TestRequestA: comment line 1"})
@DtoRequest(name = "TESTREQA",
        usedByApplications = {"app-test"},
        expectedResponses = {TestResponseA.class}
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequestA {

    @DtoField(length = 10)
    public String myRequestField;

}

