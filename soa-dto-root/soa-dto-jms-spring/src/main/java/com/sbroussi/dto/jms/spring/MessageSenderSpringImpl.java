package com.sbroussi.dto.jms.spring;

import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;
import com.sbroussi.dto.jms.JmsException;
import com.sbroussi.dto.jms.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * A sender that delegates to Spring-JMS implementation.
 */
public class MessageSenderSpringImpl implements MessageSender {

    @Autowired
    private JmsTemplate jmsTemplate;


    public void sendMessage(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        String correlationId = sendMessage(jmsContext.getRequestQueue(), request.getRawRequest(), jmsTemplate);

        // ONE-WAY request (fire and forget) ?
        if ((!request.isOneWayRequest() && (correlationId != null) && (correlationId.length() > 0))) {
            // read responses if a correlation ID is returned
        }
    }

    private String sendMessage(final Queue requestQueue,
                               final String message,
                               final JmsTemplate jmsTemplate) {
        String correlationId = null;
        MyMessageCreator messageCreator = new MyMessageCreator(message);
        try {
            jmsTemplate.send(requestQueue, messageCreator);
            correlationId = messageCreator.message.getJMSMessageID();
        } catch (Exception e) {
            throw new JmsException("cannot send JMS message [" + message + "]", e);
        }

        return correlationId;
    }

    /**
     * Spring-JMS hook to catch the JMS message to be able read the 'correlationId'.
     */
    private static class MyMessageCreator implements MessageCreator {

        private String messageString;

        private Message message;

        public MyMessageCreator(final String messageString) {
            this.messageString = messageString;
        }

        @Override
        public Message createMessage(final Session session) throws JMSException {
            // keep a local reference to read the 'correlationId'
            message = session.createTextMessage(messageString);
            return message;
        }
    }
}
