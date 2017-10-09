package com.sbroussi.app1;

import com.sbroussi.dto.jms.DtoJmsRequest;
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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class App1Test {


    @Test
    public void testSend() throws Exception {

        String expectedResponseHeader = "@WEB    0000143HEADER120000044flaghostuserId12userProfile1fsidmyLaptop    "
                + "ADRVIRTU" // response name
                + "0004401request1                    9";
        // 2 messages of 15 characters
        String response = "00002" + "00015" + "Hello World    " + "Have a nice day";

        // MOCK context (see MessageSenderImpl.java implementation)
        Queue requestQueue = mock(Queue.class);
        Queue replyQueue = mock(Queue.class);
        when(requestQueue.getQueueName()).thenReturn("MQ.QUEUE.WRITE");
        when(replyQueue.getQueueName()).thenReturn("MQ.QUEUE.READ");
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

        // setup
        App1 app = new App1();
        app.setQueueFactory(queueFactory);
        app.setRequestQueue(requestQueue);
        app.setReplyQueue(replyQueue);

        // send
        DtoJmsRequest request = app.sendText("hello");
        assertNotNull(app.getDtoContext());
        assertNotNull(app.getJmsContext());
        assertNotNull(request);


        String expectedHeader = "@WEB    0000129HEADER  00000440000S000user1   profilA                     ADRVIRTU0000055";
        String expectedData = "formatted DTO: [com.sbroussi.xml.request.v1_0.ADRVIRTU]";

        assertEquals(expectedHeader + expectedData, request.getRawRequest());

        assertEquals(expectedResponseHeader + response, request.getRawResponse());


    }
}
