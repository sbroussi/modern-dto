package com.sbroussi.soa;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.soa.dialect.DialectString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SoaDtoRequestFactoryTest {

    @Test
    public void testCreate() {
        SoaDtoRequestFactory factory = new SoaDtoRequestFactory(null);
        assertNull(factory.create(this).getSoaContext());


        SoaContext soaContext = SoaContext.builder()
                .dtoContext(new DtoContext())
                .applicationId("my app for testing")
                .dialect(new DialectString())
                .build();

        // set 'context' in the factory
        factory = new SoaDtoRequestFactory(soaContext);

        SoaDtoRequest request = factory.create(this);
        // check that the populated context is the expected one
        assertNotNull(request.getSoaContext());
        assertEquals("my app for testing", request.getSoaContext().getApplicationId());

    }
}
