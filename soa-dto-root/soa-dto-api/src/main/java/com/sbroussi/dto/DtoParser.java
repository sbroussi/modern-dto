package com.sbroussi.dto;

public interface DtoParser {


    <T> T parse(Class<T> clazz, String input);


}
