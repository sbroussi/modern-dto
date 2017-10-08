package com.sbroussi.dto;

import com.sbroussi.dto.mapper.DatatypeMappers;

public class DtoFormatterImpl implements DtoFormatter {

    private final DatatypeMappers datatypeMappers;

    public DtoFormatterImpl(DatatypeMappers datatypeMappers) {
        this.datatypeMappers = datatypeMappers;
    }


    @Override
    public String format(final Object dto) {

        return "formatted DTO: [" + dto.getClass().getName() + "]";
    }
}
