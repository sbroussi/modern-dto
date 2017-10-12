package com.sbroussi.soa;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.soa.dialect.DialectString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SoaDtoRequestStaticContextTest {

    @Test
    public void testCreate() {

        assertNull(new SoaDtoRequestStaticContext(this).getSoaContext());


        SoaContext soaContext = SoaContext.builder()
                .dtoContext(new DtoContext())
                .applicationId("my app for testing")
                .dialect(new DialectString())
                .build();

        // set 'static' context
        SoaDtoRequestStaticContext.setStaticSoaContext(soaContext);

        SoaDtoRequestStaticContext request = new SoaDtoRequestStaticContext(this);
        // check that the populated context is the expected one
        assertNotNull(request.getSoaContext());
        assertEquals("my app for testing", request.getSoaContext().getApplicationId());

    }
}
