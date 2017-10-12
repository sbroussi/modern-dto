package com.sbroussi.dto.test.testA;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;
import lombok.Setter;

@DtoResponse(name = "TESTREPA")
@Getter
@Setter
public class TestResponseA {

    @DtoField(length = 15)
    public String myResponseField;

}
