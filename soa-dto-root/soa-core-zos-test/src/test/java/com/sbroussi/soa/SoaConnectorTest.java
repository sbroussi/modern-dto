package com.sbroussi.soa;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.transport.MockSender;
import com.sbroussi.soa.audit.AuditorVerboseLogger;
import com.sbroussi.soa.zos.ZosDialect;
import com.sbroussi.soa.zos.test.TestRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SoaConnectorTest {

    @Test
    public void testSend() throws Exception {

        // ------------ MOCK request / response

        String mockRequest = "@WEB    0000128HEADER  00000440000S000user1   profilA                     TEST_REQ0000054formatted DTO: [com.sbroussi.soa.zos.test.TestRequest]";
        String expectedResponseHeader = "@WEB    0000143HEADER  00000440000S000userId  profile         myLaptop    "
                + "TEST_REP" // response name
                + "0004401request1                    9";
        // 2 messages of 15 characters
        String expectedResponse = "00002" + "00015" + "Hello World    " + "Have a nice day";

        MockSender mockSender = new MockSender();
        mockSender.mock(mockRequest, expectedResponseHeader + expectedResponse);


        // ------------

        // setup JMS context
        DtoContext dtoContext = new DtoContext();

        SoaContext soaContext = SoaContext.builder()
                .dtoContext(dtoContext)
                .applicationId("app-test")
                .dialect(new ZosDialect("@WEB")) // fixed-width fields with header
                .messageSender(mockSender) // mock responses
                .build();

        // add a verbose logger for DEBUG
        soaContext.getAuditors().add(new AuditorVerboseLogger());


        // JMS request
        TestRequest adrvirtu = TestRequest.builder()
                .myRequestField("test")
                .build();

        // send JMS request and read response (if any)
        SoaDtoRequest request = new SoaDtoRequest(soaContext, adrvirtu);
        request.setUserId("user1");
        request.setUserProfile("profilA");

        // ----------------- send via 'SoaConnector.send'
        request.execute();

        String expectedHeader = "@WEB    0000128HEADER  00000440000S000user1   profilA                     TEST_REQ0000054";
        String expectedData = "formatted DTO: [com.sbroussi.soa.zos.test.TestRequest]";

        assertEquals(expectedHeader + expectedData, request.getRawRequest());
        assertEquals(expectedResponseHeader + expectedResponse, request.getRawResponse());


    }
}
