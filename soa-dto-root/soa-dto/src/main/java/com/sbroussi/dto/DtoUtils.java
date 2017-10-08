package com.sbroussi.dto;


import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoFieldReference;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DtoUtils {

    public static List<Annotation> readFieldsAnnotation(Class clazz) throws Exception {

        ArrayList<Annotation> result = new ArrayList<Annotation>();

        Field[] fields = clazz.getFields();

        for (Field field : fields) {

            DtoField dtoField = readDtoField(field);
            if (dtoField != null) {
                result.add(dtoField);
            }
        }


        return result;
    }

    private static DtoField readDtoField(final AnnotatedElement element) {
        DtoField dtoField = element.getAnnotation(DtoField.class);
        if (dtoField == null) {
            // not defined: search for reference
            final DtoFieldReference dtoFieldRef = element.getAnnotation(DtoFieldReference.class);
            if (dtoFieldRef != null) {
                // recursive call
                dtoField = readDtoField(dtoFieldRef.reference());
            }
        }
        return dtoField;
    }


}
