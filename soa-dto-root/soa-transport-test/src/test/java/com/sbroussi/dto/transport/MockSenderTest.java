package com.sbroussi.dto.transport;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MockSenderTest {


    @Test
    public void testSend() throws Exception {

        MockSender messageSender = new MockSender();
        messageSender.mock("my request", "my response");

        // send
        messageSender.send("my request");
        messageSender.send("my request"); // 2 identical calls are OK

        // sendAndReceive
        assertEquals("my response", messageSender.sendAndReceive("my request", 100L));
        assertEquals("my response", messageSender.sendAndReceive("my request", 100L)); // 2 identical calls are OK
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSendDuplicate() throws Exception {
        MockSender messageSender = new MockSender();
        messageSender.mock("my request", "my response");

        messageSender.mock("my request", "my response 2"); // cannot register duplicate request

    }

}
