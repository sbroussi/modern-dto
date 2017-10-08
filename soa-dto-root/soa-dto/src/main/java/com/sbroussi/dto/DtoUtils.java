package com.sbroussi.dto;


import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.annotations.DtoFieldNumber;
import com.sbroussi.dto.annotations.DtoFieldNumberReference;
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
            DtoFieldNumber dtoFieldNumber = readDtoFieldNumber(field);
            if (dtoFieldNumber != null) {
                result.add(dtoFieldNumber);
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


    private static DtoFieldNumber readDtoFieldNumber(final AnnotatedElement element) {
        DtoFieldNumber DtoFieldNumber = element.getAnnotation(DtoFieldNumber.class);
        if (DtoFieldNumber == null) {
            // not defined: search for reference
            final DtoFieldNumberReference DtoFieldNumberRef = element.getAnnotation(DtoFieldNumberReference.class);
            if (DtoFieldNumberRef != null) {
                // recursive call
                DtoFieldNumber = readDtoFieldNumber(DtoFieldNumberRef.reference());
            }
        }
        return DtoFieldNumber;
    }

    /**
     * @param array       the array to lookup
     * @param valueToFind the value to find
     * @return TRUE if found
     */
    public static boolean contains(final String[] array, final String valueToFind) {
        if ((array != null) && (valueToFind != null)) {
            for (int i = 0; i < array.length; i++) {
                if (valueToFind.equals(array[i])) {
                    return true;
                }
            }

        }
        return false;
    }

}
