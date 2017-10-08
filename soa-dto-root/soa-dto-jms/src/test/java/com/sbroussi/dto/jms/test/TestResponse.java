package com.sbroussi.dto.jms.test;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;
import lombok.Setter;

@DtoResponse(name = "TEST_REP")
@Getter
@Setter
public class TestResponse {

    @DtoField(length = 32)
    public String myResponseField;

}