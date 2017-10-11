package com.sbroussi.app1;

import com.sbroussi.dto.transport.MockSender;
import com.sbroussi.soa.SoaDtoRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class App1Test {


    @Test
    public void testSend() throws Exception {

        // ------------ MOCK request / response

        String mockRequest = "@WEB    0000129HEADER  00000440000S000user1   profilA                     ADRVIRTU0000055formatted DTO: [com.sbroussi.xml.request.v1_0.ADRVIRTU]";
        String expectedResponseHeader = "@WEB    0000143HEADER  00000440000S000userId  profile         myLaptop    "
                + "ADRVIRTU" // response name
                + "0004401request1                    9";
        // 2 messages of 15 characters
        String expectedResponse = "00002" + "00015" + "Hello World    " + "Have a nice day";

        MockSender mockSender = new MockSender();
        mockSender.mock(mockRequest, expectedResponseHeader + expectedResponse);

        // ------------


        // setup
        App1 app = new App1();

        // inject a 'Mock' sender for this Unit-Test
        app.getSoaContext().setMessageSender(mockSender);

        // send
        SoaDtoRequest request = app.sendText("hello");
        assertNotNull(app.getSoaContext());
        assertNotNull(request);


        String expectedHeader = "@WEB    0000129HEADER  00000440000S000user1   profilA                     ADRVIRTU0000055";
        String expectedData = "formatted DTO: [com.sbroussi.xml.request.v1_0.ADRVIRTU]";

        assertEquals(expectedHeader + expectedData, request.getRawRequest());

        assertEquals(expectedResponseHeader + expectedResponse, request.getRawResponse());


    }
}
