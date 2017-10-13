package com.sbroussi.dto.test.testA;

import com.sbroussi.dto.annotations.DtoComment;
import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;
import lombok.Setter;

@DtoComment(notes = {
        "", // first empty line will be ignored
        "TestResponseA: comment line 1",
        "TestResponseA: comment line 2"})
@DtoResponse(name = "TESTREPA")
@Getter
@Setter
public class TestResponseA {

    @DtoField(length = 15)
    public String myResponseField;

}
