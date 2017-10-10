package com.sbroussi.dto;

/**
 * A DTO parser to unmarshal String to Java beans.
 */
public interface DtoParser {


    <T> T parse(Class<T> clazz, String input);


}
