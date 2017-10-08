package com.sbroussi.app1;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;
import com.sbroussi.dto.jms.DtsJmsAgent;
import com.sbroussi.soa.dto.Apps;
import com.sbroussi.xml.request.v1_0.ADRVIRTU;

public class App1 {

    private final DtoContext dtoContext;
    private final DtoJmsContext jmsContext;

    public App1() {
        dtoContext = DtoContext.builder().build();
        jmsContext = DtoJmsContext.builder()
                .dtoContext(dtoContext)
                .applicationId(Apps.app1).build();
    }


    public String sendText(final String text) {

        ADRVIRTU adrvirtu = ADRVIRTU.builder()
                .myRequestField1(text)
                .build();

        DtoJmsRequest request = new DtoJmsRequest(adrvirtu);
        return DtsJmsAgent.send(jmsContext, request);

    }

}
