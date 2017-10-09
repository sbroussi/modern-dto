package com.sbroussi.dto.error;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoResponse;

/**
 * Standard error response: ERROR.
 */
@DtoResponse(name = "ERROR")
public class ERROR {

    @DtoField(length = 10)
    private String message;
}
