package com.sbroussi.dto.mapper;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class maintains the list of all registered DataType mappers to format and parse String, Integer, BigDecimal....
 * <p>
 * Is is recommended to keep one instance of this class in your application,
 */
public class DatatypeMappers {

    /**
     * The map of DataType mappers.
     * <p>
     * key: classname + ";" + lentgh of the field ('0' when not applicable -but useful for date with different formats-)
     */
    private ConcurrentHashMap<String, DatatypeMapper> mappers = new ConcurrentHashMap<String, DatatypeMapper>();

    public DatatypeMappers() {
        register(String.class, new MapperString());
        register(Integer.class, new MapperInteger());
        register(Date.class, new MapperDate("yyyyMMdd", 8,
                new String[]{"00000000", "99991231", "99999999"}));
        register(Date.class, new MapperDate("yyyy-MM-dd'T'HH:mm:ss",
                10 + 1 + 8,
                new String[]{"0000-00-00T00:00:00", "9999-12-31T00:00:00", "9999-99-99T00:00:00"}));
    }

    /**
     * Register a datatype mapper,
     *
     * @param clazz  the class to map
     * @param mapper the datatype mapper
     */
    public void register(Class<?> clazz, DatatypeMapper mapper) {
        if (mapper instanceof DatatypeMapperOnLength) {
            register(clazz, mapper, ((DatatypeMapperOnLength) mapper).getFieldLength());
        } else {
            register(clazz, mapper, 0);
        }
    }

    private void register(Class<?> clazz, DatatypeMapper mapper, int fieldLength) {
        final String key = clazz.getName() + ";" + fieldLength;
        mappers.put(key, mapper);
    }

    private DatatypeMapper lookup(Class<?> clazz) {
        return lookup(clazz, 0);
    }

    private DatatypeMapper lookup(Class<?> clazz, int fieldLength) {
        final String key = clazz.getName() + ";" + fieldLength;
        return mappers.get(key);
    }

}
