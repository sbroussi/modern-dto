package com.sbroussi.dto.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface DtoFieldNumber {

    int length();

    char paddingChar() default '0';

    int fractionDigits();

    boolean withSign() default false;

}
