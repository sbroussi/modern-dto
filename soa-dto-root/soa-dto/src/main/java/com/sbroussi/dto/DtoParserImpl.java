package com.sbroussi.dto;

import com.sbroussi.dto.mapper.DatatypeMappers;

public class DtoParserImpl implements DtoParser {

    private final DatatypeMappers datatypeMappers;

    public DtoParserImpl(final DatatypeMappers datatypeMappers) {
        this.datatypeMappers = datatypeMappers;
    }


    @Override
    public <T> T parse(final Class<T> clazz, final String input) {

        // TODO: implement real parser
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("TODO: need more robust parser", e);
        }

    }
}
