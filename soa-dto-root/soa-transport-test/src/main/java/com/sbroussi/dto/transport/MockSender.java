package com.sbroussi.dto.transport;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * A MOCK sender to simply unit-test application based on messages.
 */
@Getter
@Setter
public class MockSender implements MessageSender {


    /**
     * The system CR/LF on the running platform.
     */
    private String crlf = System.getProperty("line.separator");

    /**
     * TRUE to throw a 'TransportException' if the request is not found at runtime during the Unit-Test.
     * <p>
     * Defaults to TRUE.
     */
    private boolean throwErrorWhenRequestNotFound = true;


    /**
     * The mock request and response.
     * <p>
     * key: the RAW string of the request
     * <p>
     * value: the RAW string of the response
     */
    private Map<String, String> messages = new HashMap<String, String>();


    /**
     * Record a mock request and its response.
     *
     * @param request  the RAW string of the request (a check for duplicate is done)
     * @param response the RAW string of the response
     */
    public void mock(final String request, final String response) {
        // check for duplicate to help Unit-Testing
        if (messages.containsKey(request)) {
            throw new IllegalArgumentException("duplicate request detected [" + request +
                    "] cannot be added with response [" + response
                    + "] because a response is already registered [" + messages.get(request) + "]");
        }
        messages.put(request, response);
    }

    // --------------- from MessageSender
    @Override
    public void send(final String rawMessage) {
        sendMessage(rawMessage);
    }

    @Override
    public String sendAndReceive(final String rawMessage, final long timeout) {
        return sendMessage(rawMessage);
    }

    // --------------- internal implementation

    private String sendMessage(final String rawMessage) {

        final String response = messages.get(rawMessage);
        if ((throwErrorWhenRequestNotFound) && (response == null)) {

            throw new TransportException("request not found in MOCK requests [" + rawMessage + "]; " + crlf + dumpRequests());
        }
        return response;
    }

    private StringBuilder dumpRequests() {
        // dump all registered MOCK requests
        StringBuilder sb = new StringBuilder(messages.size() * 512);

        // Sort request alphabetically
        TreeSet<String> sortedRequest = new TreeSet<String>();
        sortedRequest.addAll(messages.keySet());

        sb.append("List of registered mock requests (size: " + messages.size() + "):").append(crlf);
        for (String mockRequest : sortedRequest) {
            sb.append("[").append(mockRequest).append("]").append(crlf);
        }
        return sb;
    }
}
