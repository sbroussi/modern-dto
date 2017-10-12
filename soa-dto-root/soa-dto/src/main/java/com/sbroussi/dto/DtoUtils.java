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

    /**
     * @param pattern the pattern to repeat
     * @param nbTimes the number of times to repeat the pattern
     * @return the repeated pattern or an empty string '' if nothing to repeat
     */
    public static final String repeat(final String pattern, final int nbTimes) {
        if ((pattern == null) || (pattern.length() == 0) || (nbTimes < 1)) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(pattern.length() * nbTimes + 2);
        for (int i = 0; i < nbTimes; i++) {
            sb.append(pattern);
        }
        return sb.toString();
    }


    /**
     * @param input the value to format (MUST be a positive integer)
     * @param len   the length of the field
     * @return the value aligned right and padded with zeroes ('000123' for 'alignRight(123, 6)')
     */
    public static String alignRight(final int input, final int len) {
        if (input < 0) {
            throw new IllegalArgumentException("argument 'input' is negative [" + input + "]");
        }
        return alignRight(Integer.toString(input), len, '0');
    }

    /**
     * @param input the value to format
     * @param len   the length of the field
     * @return the value aligned right and padded with the specified character ('000123' for 'alignRight(123, 6, '0')')
     */
    public static String alignRight(final String input, final int len, final char paddingChar) {
        if ((input == null) || (input.length() == 0)) {
            return repeat(" ", len);
        }
        final int length = input.length();
        if (length == len) {
            return input;
        }
        if (length > len) {
            return input.substring(0, len);
        }
        return repeat("" + paddingChar, len - length) + input;

    }

    public static String alignLeft(final String input, final int len) {
        if ((input == null) || (input.length() == 0)) {
            return repeat(" ", len);
        }
        final int length = input.length();
        if (length == len) {
            return input;
        }
        if (length > len) {
            return input.substring(0, len);
        }
        return input + repeat(" ", len - length);
    }

    /**
     * @return A String ("" empty string for 'null'),
     */
    public static String strNN(final String input) {
        if (input == null) {
            return "";
        }
        return input;
    }


    /**
     * From Spring: org.springframework.util.ClassUtils.
     *
     * @return the default ClassLoader
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignore) {
        }

        if (cl == null) {
            cl = DtoUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignore) {

                }
            }
        }

        return cl;
    }
}
