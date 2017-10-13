package com.sbroussi.soa.audit;

import com.sbroussi.soa.SoaContext;
import com.sbroussi.soa.SoaDtoRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Simple slf4j logger to dump the request in the LOG file with DEBUG level.
 * <p>
 * This logger is 'verbose' and dump a lot of information during DEV and DEBUG phases;
 * you should NOT activate this Auditor in PRODUCTION.
 */
@Slf4j
public class AuditorVerboseLogger implements Auditor {

    /**
     * The system CR/LF on the running platform.
     */
    private String CRLF = System.getProperty("line.separator");

    @Override
    public void traceBeforeRequest(final SoaDtoRequest request, final Map<String, Object> data) {
        if (log.isDebugEnabled()) {
            String rawMessage = request.getRawRequest();
            if (rawMessage == null) {
                rawMessage = "";
            }


            final SoaContext soaContext = request.getSoaContext();
            String requestName = request.getDtoRequestAnnotation().name();


            String rawData = rawMessage;
            String header = null;
            final String applicationId = soaContext.getApplicationId();
            if ((applicationId != null) && (rawData.length() >= 89) && (rawData.startsWith(applicationId))) {
                // @CHANNEL0000090HEADER  000004400000000SB758   TEST                        REQUEST 00000161081080
                // 0        1         2         3         4         5         6         7         8         9
                // 1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
                // -----------------------------------------------------------------------------------------1081080
                header = rawData.substring(0, 89);
                rawData = rawData.substring(89);
            }

            final StringBuilder sb = new StringBuilder(rawData.length() + 512);
            sb.append("SOA REQUEST CALL:").append(CRLF);
            sb.append("*").append(CRLF);
            sb.append("*").append(CRLF);
            sb.append("* SOA REQUEST NAME  : [").append(requestName).append("]").append(CRLF);
            if (header != null) {
                sb.append("* SOA REQUEST HEADER: [").append(header).append("]").append(CRLF);
            }
            sb.append("* SOA REQUEST DATA  : [").append(rawData).append("]").append(CRLF);
            sb.append("* SOA DATA  LENGTH  : [").append(rawData.length()).append("]").append(CRLF);
            sb.append("*").append(CRLF);
            sb.append("*");
            log.debug(sb.toString());
        }
    }


    @Override
    public void traceAfterRequest(final SoaDtoRequest request, final Map<String, Object> data) {
        if (log.isDebugEnabled()) {
            String rawMessage = request.getRawResponse();
            if (rawMessage == null) {
                rawMessage = "";
            }

            log.debug("Received response of request [" + request.getDtoRequestAnnotation().name()
                    + "] received (" + rawMessage.length() + " chars): [" + rawMessage + "]");
        }
    }

    @Override
    public void traceOnTransportError(final SoaDtoRequest request, final Map<String, Object> data, final Throwable cause) {
        log.error("ERROR while sending request [" + request.getDtoRequestAnnotation().name() + "]", cause);
    }

    @Override
    public void traceAfterResponseParsing(final SoaDtoRequest request, final Map<String, Object> data) {
        // nothing to do
    }

    @Override
    public void traceClose(final SoaDtoRequest request, final Map<String, Object> data, final Throwable cause) {
        // nothing to do
    }

}
