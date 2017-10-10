package com.sbroussi.dto;

import com.sbroussi.dto.mapper.DatatypeMappers;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoContext {

    private DatatypeMappers datatypeMappers = new DatatypeMappers();

    private DtoFormatter dtoFormatter = new DtoFormatterImpl(getDatatypeMappers());

    private DtoParser dtoParser = new DtoParserImpl(getDatatypeMappers());

    private DtoCatalog dtoCatalog = new DtoCatalog();

}
