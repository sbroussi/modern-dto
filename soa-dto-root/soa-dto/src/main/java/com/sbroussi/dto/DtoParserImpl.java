package com.sbroussi.dto;

import com.sbroussi.dto.annotations.DtoChoice;
import com.sbroussi.dto.mapper.DatatypeMappers;

import java.lang.reflect.Field;

public class DtoParserImpl implements DtoParser {

    private final DatatypeMappers datatypeMappers;

    public DtoParserImpl(final DatatypeMappers datatypeMappers) {
        this.datatypeMappers = datatypeMappers;
    }


    /**
     * @return the field annotated with '@DtoChoice' that exactly match the 'switchCharacter'
     * or return 'null' if no exact match has been found.
     */
    private Field findDtoChoiceThatExactlyMatch(final Class<?> clazz, final char switchCharacter) {
        for (final Field field : clazz.getFields()) {
            final DtoChoice dtoChoice = field.getAnnotation(DtoChoice.class);
            if ((dtoChoice != null) && (switchCharacter == dtoChoice.parseWhenNextCharacterIs())) {
                // exact match has been found
                return field;
            }
        }
        // exact match has NOT been found
        return null;
    }


    @Override
    public <T> T parse(final Class<T> clazz, final String input) {

        // TODO: implement real parser
        try {

            boolean dtoChoiceHasBeenParsed = false;

            final Field[] fields = clazz.getFields();
            for (final Field field : fields) {

                // a 'switch' character has been defined ? (old legacy responses: see ABORTSelector.java)
                final DtoChoice dtoChoice = field.getAnnotation(DtoChoice.class);
                if (dtoChoice != null) {
                    if (dtoChoiceHasBeenParsed) {
                        // a 'match' has been already parsed, skip this field and let it 'null'
                        continue;
                    }

                    // read (but do not consume) the 'switch' character to select the next field to parse
                    final char switchCharacter = input.charAt(0);

                    // check if a field with this EXACT character has been defined
                    final Field matchingField = findDtoChoiceThatExactlyMatch(clazz, switchCharacter);
                    if (matchingField == null) {

                        // ------- No fields are defined with this EXACT match

                        // Is this field the DEFAULT choice with: @DtoChoice(parseWhenNextCharacterIs = '*')
                        if (dtoChoice.parseWhenNextCharacterIs() != '*') {
                            // this is NOT the DEFAULT choice
                            // skip this field and let it 'null', try with the next one
                            continue;
                        }

                        // this field with the DEFAULT choice '*' will be parsed now

                    } else {

                        // ------- An EXACT match has been found

                        // is it this field ?
                        if (dtoChoice.parseWhenNextCharacterIs() != switchCharacter) {
                            // this is NOT the field defined with this EXACT match
                            // skip this field and let it 'null', try with the next one
                            continue;
                        }

                        // this field with the EXACT choice will be parsed now
                    }

                    dtoChoiceHasBeenParsed = true;
                }

            }

            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("TODO: need more robust parser", e);
        }

    }
}
