package com.sbroussi.dto.mapper;

public interface DatatypeMapperOnLength<K> extends DatatypeMapper<K> {

    /**
     * @return the supported length to parse or format;
     * <p>
     * For example, to format and parse dates; you may have different implementations with different date formats:
     * <p>
     * - yyyyMMdd
     * <p>
     * - yyyyMMddHHmmss
     * <p>
     * - yyyyMMddHHmmssSS
     * <p>
     * - yyyy-MM-dd
     * <p>
     * - yyyy-MM-ddTHH:mm:ss
     * <p>
     * - yyyy-MM-ddTHH:mm:ss,SS
     */
    int getFieldLength();

}
