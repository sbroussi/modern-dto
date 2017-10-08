package com.sbroussi.dto.mapper;

public interface DatatypeMapper<K> {

    String format(K input) throws Exception;

    K parse(String input) throws Exception;
}
