package com.sbroussi.dto.jms;

import lombok.extern.slf4j.Slf4j;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * A simple sender that read/write response on JMS queues.
 */
@Slf4j
public class MessageSenderImpl implements MessageSender {

    /**
     * The JMS queue factory.
     */
    private QueueConnectionFactory factory;


    /**
     * The JMS queue where to PUT the request.
     */
    private Queue requestQueue;

    /**
     * The JMS queue where to READ the responses ('null' for ONE-WAY request).
     */
    private Queue replyQueue;


    public MessageSenderImpl(final QueueConnectionFactory factory,
                             final Queue requestQueue,
                             final Queue replyQueue) {
        this.factory = factory;
        this.requestQueue = requestQueue;
        this.replyQueue = replyQueue;

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

        String rawResponse = null;
        QueueConnection connection = null;
        QueueSession session = null;
        try {

            connection = factory.createQueueConnection();
            connection.start();
            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            final TextMessage outMessage = session.createTextMessage();
            // outMessage.setJMSExpiration(expiration); // TODO: expiration ?
            if (!isOneWayRequest) {
                outMessage.setJMSReplyTo(replyQueue);
            }
            outMessage.setText(rawMessage);


            QueueSender sender = session.createSender(requestQueue);

            if (log.isTraceEnabled()) {
                log.trace("Send JMS message [" + rawMessage + "]");
            }

            // send
            sender.send(outMessage);

            final String correlationId = outMessage.getJMSMessageID();


            // ONE-WAY request (fire and forget) ?
            if (isOneWayRequest) {
                return null;
            }

            // read responses if a correlation ID is returned

            if ((correlationId == null) || (correlationId.trim().length() == 0)) {
                throw new JmsException("Expected a response but no correlationId is returned");
            }

            final String correlationIdFilter = "JMSCorrelationID = '" + correlationId + "'";
            final QueueReceiver receiver = session.createReceiver(replyQueue, correlationIdFilter);

            if (log.isDebugEnabled()) {
                log.debug("Waiting for reply message with timeout: [" + timeout
                        + " ms]; correlationId [" + correlationId + "]");
            }

            // hangs until the message arrives (or timeout)
            final Message jmsMessage = receiver.receive(timeout);
            if (jmsMessage == null) {
                throw new JmsTimeoutException("No reply message with correlationId [" + correlationId
                        + "]. Probably timed-out (waited " + timeout + " ms)");
            }
            if (!(jmsMessage instanceof TextMessage)) {
                throw new JmsException("Expected a 'TextMessage' but received a ["
                        + jmsMessage.getClass().getName() + "]");
            }
            rawResponse = ((TextMessage) jmsMessage).getText();

            if (log.isTraceEnabled()) {
                log.trace("Received JMS response [" + rawResponse
                        + "] with correlationId [" + correlationId + "]");
            }


        } catch (Throwable t) {
            throw new JmsException("cannot send JMS message [" + rawMessage + "]", t);
        }
        return rawResponse;
    }
}
