package com.sbroussi.dto;

import com.sbroussi.dto.mapper.DatatypeMappers;
import lombok.Getter;
import lombok.Setter;


/**
 * This wrapper maintains all useful helpers to manipulate DTOs.
 * <p>
 * Is is recommended to keep one instance of this class in your application,
 */
@Getter
@Setter
public class DtoContext {

    /**
     * This catalog maintains a map of all DTOs that are being used ('@DtoRequest' or '@DtoResponse').
     */
    private DtoCatalog dtoCatalog = new DtoCatalog();

    /**
     * This class maintains the list of all registered DataType mappers to format and parse String, Integer, BigDecimal....
     */
    private DatatypeMappers datatypeMappers = new DatatypeMappers();

    /** A DTO formatter to marshal Java beans to String. */
    private DtoFormatter dtoFormatter = new DtoFormatterImpl(getDatatypeMappers());

    /** A DTO parser to unmarshal String to Java beans. */
    private DtoParser dtoParser = new DtoParserImpl(getDatatypeMappers());

}
