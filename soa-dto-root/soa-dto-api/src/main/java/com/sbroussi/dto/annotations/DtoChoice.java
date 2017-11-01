package com.sbroussi.dto.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For old legacy responses: a same response may return 2 different inner responses.
 * <p>
 * The response starts with a 'switch' characters (" ", "E", "M"...) that must be read to
 * identify the real content of this response.
 * <p>
 * Example of 'Choice': response contains N sub-responses
 * + ABORTSelector:
 * + ABORT (if starts with any character, '*')
 * + ABORT_KO (if starts with 'E')
 * <p>
 * The corresponding Java code is:
 * <p>
 * <pre>
 * public class ABORTSelector {
 *
 *    @DtoChoice(parseWhenNextCharacterIs = '*')
 *    private ABORT abort;
 *
 *    @DtoChoice(parseWhenNextCharacterIs = 'E')
 *    protected ABORTKO abortko;
 * }
 * </pre>
 * <p>
 * In this example, we can have the following RAW responses:
 * <p>
 * - ' error message 1' : will be parsed as 'ABORT' with a first char ' ' and the string 'error message 1'.
 * <p>
 * - 'Eerror message 2' : will be parsed as 'ABORT_KO' with a first char 'E' and the string 'error message 2'.
 *
 * @deprecated This annotation is used for LEGACY OLD RESPONSES, do not use it for new responses.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DtoChoice {

    /**
     * @return the 'switch character' to select a choice of inner responses.
     * <p>
     * Please note that the 'switch' character is not fetched and will be the FIRST character of the matching response.
     * <p>
     * Defaults to '*': any character.
     */
    char parseWhenNextCharacterIs() default '*';

}
