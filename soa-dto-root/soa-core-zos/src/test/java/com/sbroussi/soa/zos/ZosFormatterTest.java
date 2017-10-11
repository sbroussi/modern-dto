package com.sbroussi.soa.zos;

import com.sbroussi.dto.annotations.DtoRequest;
import com.sbroussi.soa.SoaDtoRequest;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZosFormatterTest {

    @Test
    public void testFormatHeader() {

        ZosFormatter zosFormatter = new ZosFormatter();

        final SoaDtoRequest request = new SoaDtoRequest(null, null);
        final String channel = "@WEB";
        final String data = "my data";
        request.setUserId("userId");
        request.setUserProfile("profile");
        request.setUserComputerName("myLaptop");
        //request.setPersonNumber("123456");

        DtoRequest annotation = mock(DtoRequest.class);
        when(annotation.name()).thenReturn("ADRVIRTU");
        request.setDtoRequestAnnotation(annotation);

        Assert.assertEquals("@WEB    0000081HEADER  00000440000S000userId  profile         myLaptop    "
                        + "ADRVIRTU0000007my data",
                zosFormatter.formatWithHeader(request, channel, "my data"));


    }

}
