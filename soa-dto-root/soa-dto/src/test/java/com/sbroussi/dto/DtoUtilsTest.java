package com.sbroussi.dto;

import com.sbroussi.dto.annotations.DtoField;
import com.sbroussi.dto.test.MyBean;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test: DtoUtils.
 */
public class DtoUtilsTest {

    @Test
    public void testReadAnnotation() throws Exception {

        List<Annotation> result = DtoUtils.readFieldsAnnotation(MyBean.class);
        assertNotNull(result);
        assertEquals(1, result.size());

        DtoField dtoField = (DtoField) result.get(0);
        assertEquals(8, dtoField.length());

    }

    @Test
    public void testAlignRightString() {
        assertEquals("    ", DtoUtils.alignRight(null, 4, ' '));
        assertEquals("    ", DtoUtils.alignRight("", 4, ' '));
        assertEquals("   a", DtoUtils.alignRight("a", 4, ' '));
        assertEquals("  ab", DtoUtils.alignRight("ab", 4, ' '));
        assertEquals(" abc", DtoUtils.alignRight("abc", 4, ' '));
        assertEquals("abcd", DtoUtils.alignRight("abcd", 4, ' '));
        assertEquals("abcd", DtoUtils.alignRight("abcde", 4, ' '));// truncated

        assertEquals(" ab ", DtoUtils.alignRight(" ab ", 4, ' ')); // not trimmed
        assertEquals(" ab ", DtoUtils.alignRight(" ab ", 4, 'x')); // not trimmed
        assertEquals("xxxx ab ", DtoUtils.alignRight(" ab ", 8, 'x')); // not trimmed

        assertEquals("xxxa", DtoUtils.alignRight("a", 4, 'x'));
    }

    @Test
    public void testAlignRightPositiveInteger() {
        assertEquals("0", DtoUtils.alignRight(0, 1));
        assertEquals("0000", DtoUtils.alignRight(0, 4));
        assertEquals("0123", DtoUtils.alignRight(123, 4));
        assertEquals("1234", DtoUtils.alignRight(12345, 4)); // truncated
    }


    @Test
    public void testAlignLeft() {
        assertEquals("    ", DtoUtils.alignLeft(null, 4));
        assertEquals("    ", DtoUtils.alignLeft("", 4));
        assertEquals("a   ", DtoUtils.alignLeft("a", 4));
        assertEquals("ab  ", DtoUtils.alignLeft("ab", 4));
        assertEquals("abc ", DtoUtils.alignLeft("abc", 4));
        assertEquals("abcd", DtoUtils.alignLeft("abcd", 4));
        assertEquals("abcd", DtoUtils.alignLeft("abcde", 4)); // truncated

        assertEquals(" ab ", DtoUtils.alignLeft(" ab", 4)); // not trimmed

    }
}
