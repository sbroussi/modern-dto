package com.sbroussi.dto.jms;

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

    // --------------- from MessageSender
    @Override
    public void send(final String rawMessage) {
        sendMessage(true, rawMessage, 0L);
    }

    @Override
    public String sendAndReceive(final String rawMessage, final long timeout) {
        return sendMessage(false, rawMessage, timeout);
    }

    // --------------- internal implementation

    private String sendMessage(final boolean isOneWayRequest,
                               final String rawMessage,
                               final long timeout) {


        String correlationId = sendMessage(requestQueue, rawMessage, jmsTemplate);

        // ONE-WAY request (fire and forget) ?
        if ((isOneWayRequest) || (correlationId == null) || (correlationId.length() == 0)) {
            return null;
        }

        // read response if a correlation ID is returned

        // TODO: read response with Spring-JMS (correlationId)

        // TODO: remember to use timeout...

        // TODO: log response with TRACE level
        String rawResponse = null;
        if (log.isTraceEnabled()) {
            log.trace("Received JMS response [" + rawResponse
                    + "] with correlationId [" + correlationId + "]");
        }

        throw new JmsException("TODO: read response with Spring-JMS and timeout [" + timeout + "]");

    }

    private String sendMessage(final Queue requestQueue,
                               final String message,
                               final JmsTemplate jmsTemplate) {
        String correlationId = null;
        MyMessageCreator messageCreator = new MyMessageCreator(message);
        try {


            if (log.isTraceEnabled()) {
                log.trace("Send JMS message [" + message + "]");
            }

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
