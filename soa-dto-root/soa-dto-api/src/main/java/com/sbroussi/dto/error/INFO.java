package com.sbroussi.dto.error;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * Standard technical response: INFO.
 */
@DtoResponse(name = "INFO")
@Getter
@Setter
public class INFO {

    @DtoField(length = 10)
    private String message;


}
