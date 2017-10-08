package com.sbroussi.app1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class App1Test {

    @Test
    public void testSend() {
        App1 app = new App1();

        assertEquals("formatted DTO: [com.sbroussi.xml.request.v1_0.ADRVIRTU]", app.sendText("hello"));
    }
}
