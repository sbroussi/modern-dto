package com.sbroussi.app1;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.transport.SenderJms;
import com.sbroussi.soa.SoaConnector;
import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;
import com.sbroussi.soa.dialect.DialectZos;
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

    private SoaContext soaContext;

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
            dtoContext = new DtoContext();
        }
        return dtoContext;
    }

    /**
     * @return a unique instance of a 'SoaContext'.
     */
    public SoaContext getSoaContext() {
        if (soaContext == null) {

            // simple JMS implementation
            SenderJms messageSender = new SenderJms(queueFactory, requestQueue, replyQueue);

            soaContext = SoaContext.builder()
                    .dtoContext(getDtoContext())
                    .applicationId(Apps.app1)
                    .dialect(new DialectZos("@WEB")) // fixed-width fields with header
                    .messageSender(messageSender)
                    .build();
        }
        return soaContext;
    }


    /**
     * @return the DTO request (for Unit-test purpose)
     */
    public SoaDtoRequest sendText(final String text) {

        ADRVIRTU adrvirtu = ADRVIRTU.builder()
                .myRequestField1(text)
                .build();

        // send message
        SoaDtoRequest request = new SoaDtoRequest(adrvirtu);
        request.setUserId("user1");
        request.setUserProfile("profilA");
        SoaConnector.send(getSoaContext(), request);

        return request;

    }

}
