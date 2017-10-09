package com.sbroussi.dto.jms.spring;

import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;
import com.sbroussi.dto.jms.JmsException;
import com.sbroussi.dto.jms.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * A sender that delegates the JMS dialog to Spring-JMS implementation.
 * <p/>
 * We do not use the Spring annotation '@Autowired' to make it simple
 * to configure and use:
 * <p/>
 * - in 'hybrid' applications (not 100% Spring based, mixing legacy code and Spring),
 * <p/>
 * - in applications with more than one JmsTemplate.
 * <p/>
 * In both situations, Spring '@Configuration' will help you to call
 * the Constructor of this class with the desired 'JmsTemplate'.
 */
@Slf4j
public class MessageSenderSpringImpl implements MessageSender {

    private JmsTemplate jmsTemplate;

    private final Queue requestQueue;

    /**
     * Constructor.
     *
     * @param jmsTemplate  The Spring-JMS 'JmsTemplate'.
     * @param requestQueue the JMS queue to PUT the message
     */
    public MessageSenderSpringImpl(final JmsTemplate jmsTemplate,
                                   final Queue requestQueue) {
        this.jmsTemplate = jmsTemplate;
        this.requestQueue = requestQueue;
    }


    public void sendMessage(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        String correlationId = sendMessage(requestQueue, request.getRawRequest(), jmsTemplate);
        request.setCorrelationId(correlationId);

        // ONE-WAY request (fire and forget) ?
        if ((!request.isOneWayRequest()) && (correlationId != null) && (correlationId.length() > 0)) {
            // read response if a correlation ID is returned

            final String jmsName = request.getDtoRequestAnnotation().name();
            final long timeout = request.getReplyTimeoutInMs();
            if (log.isDebugEnabled()) {
                log.debug("Waiting for reply message with timeout: [" + timeout + " ms] for request ["
                        + jmsName + "] with correlationId [" + correlationId + "]");
            }


            // TODO: read response with Spring-JMS
            request.setRawResponse("TODO: read response with Spring-JMS");

            throw new JmsException("TODO: read response with Spring-JMS");
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
