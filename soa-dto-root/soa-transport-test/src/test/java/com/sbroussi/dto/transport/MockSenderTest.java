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

        // sendAndReceive
        assertEquals("my response", messageSender.sendAndReceive("my request", 100L));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testSendDuplicate() throws Exception {
        MockSender messageSender = new MockSender();
        messageSender.mock("my request", "my response");

        messageSender.mock("my request", "my response 2"); // duplicate request

    }

}
