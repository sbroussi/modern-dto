package com.sbroussi.dto.transport;

import lombok.Getter;
import lombok.Setter;

/**
 * A '/dev/null' sender that does not send and receive anything,
 * <p>
 * You can set the property 'throwError' to decide to throw an exception if this sender is called.
 */
@Getter
@Setter
public class SenderVoid implements MessageSender {

    /**
     * TRUE to throw exception if this sender is called. Defaults to TRUE.
     */
    private boolean throwError = true;

    @Override
    public void send(String message) throws TransportException {
        if (throwError) {
            throw new TransportException("SenderVoid cannot be called");
        }
    }

    @Override
    public String sendAndReceive(String message, long timeout) throws TransportException, TransportTimeoutException {
        if (throwError) {
            throw new TransportException("SenderVoid cannot be called");
        }
        return null;
    }
}
