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
    public void sendMessage(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        final boolean isOneWayRequest = request.isOneWayRequest();
        final String jmsName = request.getDtoRequestAnnotation().name();
        String rawMessage = request.getRawRequest();

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

            // remember the 'send' time
            final long start = System.currentTimeMillis();
            request.setTimestampSend(start);

            // send
            sender.send(outMessage);

            final String correlationId = outMessage.getJMSMessageID();
            request.setCorrelationId(correlationId);

            // ONE-WAY request (fire and forget) ?
            if ((!isOneWayRequest) && (correlationId != null) && (correlationId.length() > 0)) {

                // read responses if a correlation ID is returned

                final String correlationIdFilter = "JMSCorrelationID = '" + correlationId + "'";
                final QueueReceiver receiver = session.createReceiver(replyQueue, correlationIdFilter);

                final long timeout = request.getReplyTimeoutInMs();
                if (log.isDebugEnabled()) {
                    log.debug("Waiting for reply message with timeout: [" + timeout + " ms] for request ["
                            + jmsName + "] with correlationId [" + correlationId + "]");
                }

                // hangs until the message arrives (or timeout)
                final Message jmsMessage = receiver.receive(timeout);
                if (jmsMessage == null) {
                    throw new JmsTimeoutException("No reply message for request [" + jmsName
                            + "] message ID [" + correlationId
                            + "]. Probably timed-out (waited " + timeout + " ms)");
                }
                if (!(jmsMessage instanceof TextMessage)) {
                    throw new JmsException("Expected a 'TextMessage' but received a ["
                            + jmsMessage.getClass().getName() + "]");
                }
                final String rawResponse = ((TextMessage) jmsMessage).getText();
                request.setRawResponse(rawResponse);

            }

        } catch (Throwable t) {
            throw new JmsException("cannot send JMS message"
                    + "] for request [" + jmsName
                    + "] message [" + rawMessage + "]", t);
        }

    }
}
