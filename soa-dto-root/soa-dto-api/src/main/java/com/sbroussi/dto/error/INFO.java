package com.sbroussi.dto.error;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoResponse;

/**
 * Standard technical response: INFO.
 */
@DtoResponse(name = "INFO")
public class INFO {

    @DtoField(length = 10)
    private String message;
}
