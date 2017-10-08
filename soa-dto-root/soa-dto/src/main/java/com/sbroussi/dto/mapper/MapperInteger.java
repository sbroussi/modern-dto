package com.sbroussi.dto.mapper;

public class MapperInteger implements DatatypeMapper<Integer> {
    @Override
    public String format(Integer input) throws Exception {
        if (input == null) {
            return "0";
        }
        return Integer.toString(input);
    }

    @Override
    public Integer parse(String input) throws Exception {
        if ((input == null) || (input.length() == 0)) {
            return null;
        }
        return Integer.valueOf(input);
    }
}
