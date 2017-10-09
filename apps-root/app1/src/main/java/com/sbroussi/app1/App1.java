package com.sbroussi.app1;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.jms.DtoJmsConnector;
import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;
import com.sbroussi.dto.jms.MessageSenderImpl;
import com.sbroussi.dto.jms.dialect.DialectZos;
import com.sbroussi.soa.dto.Apps;
import com.sbroussi.xml.request.v1_0.ADRVIRTU;
import lombok.Getter;
import lombok.Setter;

import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;

@Getter
@Setter
public class App1 {

    private DtoContext dtoContext;

    private DtoJmsContext jmsContext;

    /**
     * The JMS Queue factory.
     */
    private QueueConnectionFactory queueFactory;

    /**
     * The JMS queue where to PUT the request.
     */
    private Queue requestQueue;

    /**
     * The JMS queue where to READ the response.
     */
    private Queue replyQueue;


    /**
     * @return a unique instance of a 'getDtoContext'.
     */
    public DtoContext getDtoContext() {
        if (dtoContext == null) {
            dtoContext = DtoContext.builder().build();
        }
        return dtoContext;
    }

    /**
     * @return a unique instance of a 'DtoJmsContext'.
     */
    public DtoJmsContext getJmsContext() {
        if (jmsContext == null) {
            jmsContext = DtoJmsContext.builder()
                    .dtoContext(getDtoContext())
                    .applicationId(Apps.app1)
                    .dialect(new DialectZos()) // fixed-width fields with header
                    .messageSender(new MessageSenderImpl(queueFactory))
                    .requestQueue(requestQueue)
                    .requestQueueName("MQ.QUEUE.WRITE")
                    .replyQueue(replyQueue)
                    .replyQueueName("MQ.QUEUE.READ")
                    .build();
        }
        return jmsContext;
    }


    /**
     * @return the DTO request (for Unit-test purpose)
     */
    public DtoJmsRequest sendText(final String text) {

        ADRVIRTU adrvirtu = ADRVIRTU.builder()
                .myRequestField1(text)
                .build();

        // send JMS message
        DtoJmsRequest request = new DtoJmsRequest(adrvirtu);
        DtoJmsConnector.send(getJmsContext(), request);

        return request;

    }

}
