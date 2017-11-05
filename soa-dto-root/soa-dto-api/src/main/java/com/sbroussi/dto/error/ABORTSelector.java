package com.sbroussi.dto.error;

import com.sbroussi.dto.annotations.DtoChoice;
import com.sbroussi.dto.annotations.DtoResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DtoResponse(name = "ABORT")
public class ABORTSelector {

    @DtoChoice(parseWhenNextCharacterIs = '*')
    private ABORT abort;

    @DtoChoice(parseWhenNextCharacterIs = 'E')
    private ABORTKO abortko;

}
