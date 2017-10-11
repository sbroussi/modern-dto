package com.sbroussi.app1;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.transport.SenderJms;
import com.sbroussi.soa.SoaConnector;
import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;
import com.sbroussi.soa.dto.Apps;
import com.sbroussi.soa.zos.ZosDialect;
import com.sbroussi.xml.request.v1_0.ADRVIRTU;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class App1 {

    private SoaContext soaContext;


    /**
     * @return the unique instance of 'SoaContext'  in this application
     */
    public SoaContext getSoaContext() {
        if (soaContext == null) {

            // simple JMS implementation
            SenderJms messageSender = null; // new SenderJms(queueFactory, requestQueue, replyQueue);

            soaContext = SoaContext.builder()
                    .dtoContext(new DtoContext())
                    .applicationId(Apps.app1)
                    .dialect(new ZosDialect("@WEB")) // fixed-width fields with header
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
        SoaDtoRequest request = new SoaDtoRequest(getSoaContext(), adrvirtu);
        request.setUserId("user1");
        request.setUserProfile("profilA");
        SoaConnector.send(request);

        return request;

    }

}
