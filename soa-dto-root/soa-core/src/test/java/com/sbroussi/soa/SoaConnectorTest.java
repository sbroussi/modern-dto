package com.sbroussi.soa;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.transport.SenderJms;
import com.sbroussi.soa.audit.AuditorVerboseLogger;
import com.sbroussi.soa.dialect.DialectZos;
import com.sbroussi.soa.test.TestRequest;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SoaConnectorTest {

    @Test
    public void testSend() throws Exception {


        String expectedResponseHeader = "@WEB    0000143HEADER  00000440000S000userId  profile         myLaptop    "
                + "TEST_REP" // response name
                + "0004401request1                    9";
        // 2 messages of 15 characters
        String response = "00002" + "00015" + "Hello World    " + "Have a nice day";


        // MOCK context (see MessageSenderImpl.java implementation)
        Queue requestQueue = mock(Queue.class);
        Queue replyQueue = mock(Queue.class);
        QueueConnectionFactory queueFactory = mock(QueueConnectionFactory.class);
        QueueConnection connection = mock(QueueConnection.class);
        when(queueFactory.createQueueConnection()).thenReturn(connection);
        QueueSession session = mock(QueueSession.class);
        when(connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        TextMessage requestMessage = mock(TextMessage.class);
        when(session.createTextMessage()).thenReturn(requestMessage);
        when(requestMessage.getJMSMessageID()).thenReturn("msgId-12345678");
        QueueSender sender = mock(QueueSender.class);
        when(session.createSender(requestQueue)).thenReturn(sender);
        QueueReceiver receiver = mock(QueueReceiver.class);
        when(session.createReceiver(Matchers.<Queue>anyObject(), anyString())).thenReturn(receiver);
        TextMessage responseMessage = mock(TextMessage.class);
        when(receiver.receive(anyLong())).thenReturn(responseMessage);
        when(responseMessage.getText()).thenReturn(expectedResponseHeader + response);

        // setup JMS context
        DtoContext dtoContext = new DtoContext();

        // simple JMS implementation
        SenderJms messageSender = new SenderJms(queueFactory, requestQueue, replyQueue);

        SoaContext soaContext = SoaContext.builder()
                .dtoContext(dtoContext)
                .applicationId("app-test")
                .dialect(new DialectZos("@WEB")) // fixed-width fields with header
                .messageSender(messageSender)
                .build();

        // add a verbose logger for DEBUG
        soaContext.getAuditors().add(new AuditorVerboseLogger());


        // JMS request
        TestRequest adrvirtu = TestRequest.builder()
                .myRequestField("test")
                .build();

        // send JMS request and read response (if any)
        SoaDtoRequest request = new SoaDtoRequest(adrvirtu);
        request.setUserId("user1");
        request.setUserProfile("profilA");

        // send
        SoaConnector.send(soaContext, request);


        String expectedHeader = "@WEB    0000124HEADER  00000440000S000user1   profilA                     TEST_REQ0000050";
        String expectedData = "formatted DTO: [com.sbroussi.soa.test.TestRequest]";

        assertEquals(expectedHeader + expectedData, request.getRawRequest());
        assertEquals(expectedResponseHeader + response, request.getRawResponse());


    }
}
