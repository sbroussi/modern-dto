package com.sbroussi.dto.mapper;

import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Mapper for: java.util.Date.
 */
public class MapperDate implements DatatypeMapperOnLength<Date> {

    /**
     * Optional: legacy Back-end support: The list of values be interpreted as 'null' when PARSING a date.
     * <p>
     * For example: "00000000", "99991231", "99999999"...
     */
    @Getter
    private final String[] nullDateValues;


    @Getter
    private final String format;

    private final int fieldLength;

    /**
     * Constructor.
     *
     * @param format The supported Date Format.
     *               <p>
     *               - yyyyMMdd
     *               <p>
     *               - yyyyMMddHHmmss
     *               <p>
     *               - yyyyMMddHHmmssSS
     *               <p>
     *               - yyyy-MM-dd
     *               <p>
     *               - yyyy-MM-ddTHH:mm:ss
     *               <p>
     *               - yyyy-MM-ddTHH:mm:ss,SS
     */
    public MapperDate(final String format) {
        this(format, format.length(), null);
    }

    /**
     * Constructor.
     *
     * @param format      The supported Date Format.
     *                    <p>
     *                    - yyyyMMdd
     *                    <p>
     *                    - yyyyMMddHHmmss
     *                    <p>
     *                    - yyyyMMddHHmmssSS
     *                    <p>
     *                    - yyyy-MM-dd
     *                    <p>
     *                    - yyyy-MM-ddTHH:mm:ss
     *                    <p>
     *                    - yyyy-MM-ddTHH:mm:ss,SS
     * @param fieldLength the length of the formatted date.
     *                    This is useful when the date format contains escape characters (for example 'T')
     */
    public MapperDate(final String format, final int fieldLength, final String[] nullDateValues) {
        this.format = format;
        this.fieldLength = fieldLength;
        this.nullDateValues = nullDateValues;
    }


    @Override
    public int getFieldLength() {
        return fieldLength;
    }

    @Override
    public String format(final Date input) throws Exception {
        if (input == null) {

            // if specified, the FIRST value will be used to return 'null' date when FORMATTING a date ?
            // for now: no, we prefer to use standard PADDING with '0' defined in the 'DtoField'
            //    // use 'null' values ?
            //    if ((nullDateValues != null) && (nullDateValues.length > 0)) {
            //        return nullDateValues[0];
            //    }

            return "";
        }


        // TODO: use Java 8 API when Java 6 is no more required
        // Java 6 SDK: The class 'SimpleDateFormat' is not multi-threads safe
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(input);
    }

    @Override
    public Date parse(String input) throws Exception {
        if ((input != null) && (input.length() == fieldLength)) {

            // check 'null' values
            if (nullDateValues != null) {
                for (int i = 0; i < nullDateValues.length; i++) {
                    if (nullDateValues[i].equals(input)) {
                        return null;
                    }
                }
            }

            // TODO: use Java 8 API when Java 6 is no more required
            // Java 6 SDK: The class 'SimpleDateFormat' is not multi-threads safe
            SimpleDateFormat parser = new SimpleDateFormat(format);
            return parser.parse(input);

        }
        return null;

    }

}
