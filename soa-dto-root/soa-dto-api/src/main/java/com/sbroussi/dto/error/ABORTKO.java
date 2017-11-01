package com.sbroussi.dto.error;

import com.sbroussi.dto.annotations.DtoField;
import lombok.Getter;
import lombok.Setter;

/**
 * Standard error response: ABORT.
 */
@Getter
@Setter
public class ABORTKO {

    @DtoField(length = 1)
    private String errorCode;

    @DtoField(length = 38)
    private String errorMessage;

}
