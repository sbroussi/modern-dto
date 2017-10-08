package com.sbroussi.dto.mapper;

public class MapperString implements DatatypeMapper<String> {

    @Override
    public String format(String input) throws Exception {
        if (input == null) {
            return "";
        }
        return input;
    }

    @Override
    public String parse(String input) throws Exception {
        return input;
    }
}
