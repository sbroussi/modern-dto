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

}
