package com.sbroussi.dto.error;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoResponse;

/**
 * Standard error response: ABORT.
 */
@DtoResponse(name = "ABORT")
public class ABORT {

    @DtoField(length = 10)
    private String message;
}
