package com.sbroussi.dto.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface DtoFieldNumber {

    DtoField dtoField() default @DtoField(length = -1, paddingChar = '0');

    int length();

    int fractionDigits();

    boolean withSign() default false;

}
