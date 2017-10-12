package com.sbroussi.dto.test.testB;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;
import lombok.Setter;

@DtoResponse(name = "TESTREPB")
@Getter
@Setter
public class TestResponseB {

    @DtoField(length = 15)
    public String myResponseField;

}
