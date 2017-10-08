package com.sbroussi.dto.annotations;


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
}
