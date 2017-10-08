package com.sbroussi.dto.jms;

import lombok.extern.slf4j.Slf4j;

import javax.jms.Message;
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

    private QueueConnectionFactory factory;


    public MessageSenderImpl(final QueueConnectionFactory factory) {
        this.factory = factory;

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
                outMessage.setJMSReplyTo(jmsContext.getReplyQueue());
            }
            outMessage.setText(rawMessage);


            QueueSender sender = session.createSender(jmsContext.getRequestQueue());


            // remember the 'send' time
            final long start = System.currentTimeMillis();
            request.setTimestampSend(start);

            // send
            sender.send(outMessage);

            final String correlationId = outMessage.getJMSMessageID();
            request.setCorrelationId(correlationId);

            if (!isOneWayRequest) {
                // expect JMS response


                final String correlationIdFilter = "JMSCorrelationID = '" + correlationId + "'";
                final QueueReceiver receiver = session.createReceiver(jmsContext.getReplyQueue(), correlationIdFilter);

                final long timeout = request.getReplyTimeoutInMs();
                if (log.isDebugEnabled()) {
                    log.debug("Waiting for reply message with time-out: [" + timeout + " ms] for request ["
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
                    throw new JmsException("Expected a TextMessage but received a ["
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
