package com.sbroussi.dto;

/**
 * A DTO formatter to marshal Java beans to String.
 */
public interface DtoFormatter {

    String format(Object dto);

}
