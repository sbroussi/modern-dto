package com.sbroussi.dto.annotations;


import com.sbroussi.dto.error.ABORTSelector;
import com.sbroussi.dto.error.ERROR;
import com.sbroussi.dto.error.INFO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DtoRequest {

    String name();

    Class[] expectedResponses();

    /**
     * List of IDs of applications that use this request.
     * <p>
     * The caller 'applicationId' will be checked at runtime (and rejected if not listed here).
     * <p>
     * This helps to generate a useful SOA catalog.
     */
    String[] usedByApplications();

    /**
     * @return the list of standard ERROR responses or technical messages that may always be returned.
     * <p>
     * Defaults to: ERROR, ABORT, INFO..
     */
    Class[] technicalResponses() default {ABORTSelector.class, ERROR.class, INFO.class};

}
