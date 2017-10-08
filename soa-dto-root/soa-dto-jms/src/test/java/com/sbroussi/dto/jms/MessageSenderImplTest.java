package com.sbroussi.dto.jms;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.jms.test.TestRequest;
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

public class MessageSenderImplTest {


    @Test
    public void testSend() throws Exception {

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
        when(responseMessage.getText()).thenReturn("my reply response");

        // setup
        DtoContext dtoContext = DtoContext.builder().build();

        MessageSenderImpl messageSender = new MessageSenderImpl(queueFactory);
        DtoJmsContext jmsContext = DtoJmsContext.builder()
                .dtoContext(dtoContext)
                .applicationId("app-test")
                .messageSender(messageSender)
                .requestQueue(requestQueue)
                .replyQueue(replyQueue)
                .build();


        // JMS request
        TestRequest adrvirtu = TestRequest.builder()
                .myRequestField("test")
                .build();

        // send JMS message
        DtoJmsRequest request = new DtoJmsRequest(adrvirtu);
        DtoJmsConnector.send(jmsContext, request);

        // send
        messageSender.sendMessage(jmsContext, request);

        assertEquals("formatted DTO: [com.sbroussi.dto.jms.test.TestRequest]", request.getRawRequest());
        assertEquals("my reply response", request.getRawResponse());


    }
}
