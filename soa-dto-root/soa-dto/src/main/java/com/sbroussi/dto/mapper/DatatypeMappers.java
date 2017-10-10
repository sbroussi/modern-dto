package com.sbroussi.dto.mapper;

import java.util.concurrent.ConcurrentHashMap;


/**
 * This class maintains the list of all registered DataType mappers to format and parse String, Integer, BigDecimal....
 * <p>
 * Is is recommended to keep one instance of this class in your application,
 */
public class DatatypeMappers {

    private ConcurrentHashMap<Class, DatatypeMapper> mappers = new ConcurrentHashMap<Class, DatatypeMapper>();

    public DatatypeMappers() {
        register(String.class, new MapperString());
        register(Integer.class, new MapperInteger());
    }

    private void register(Class<?> clazz, DatatypeMapper mapper) {
        mappers.put(clazz, mapper);
    }

    private DatatypeMapper lookup(Class<?> clazz) {
        return mappers.get(clazz);
    }

}
