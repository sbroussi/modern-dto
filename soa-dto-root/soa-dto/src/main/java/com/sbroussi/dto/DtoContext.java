package com.sbroussi.dto;

import com.sbroussi.dto.mapper.DatatypeMappers;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DtoContext {

    @Builder.Default
    private DtoFormatter dtoFormatter = new DtoFormatterImpl(new DatatypeMappers());

}
