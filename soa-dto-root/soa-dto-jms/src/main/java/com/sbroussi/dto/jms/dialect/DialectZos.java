package com.sbroussi.dto.jms.dialect;

import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.DtoFormatter;
import com.sbroussi.dto.DtoUtils;
import com.sbroussi.dto.jms.DtoJmsContext;
import com.sbroussi.dto.jms.DtoJmsRequest;

/**
 * Dialect: z/OS fixed-width fields with header.
 */
public class DialectZos implements Dialect {

    /**
     * The channel identifier required by the back-end system (@PORTAL, @WEB, @ESB...).
     */
    private String channel;

    /**
     * Constructor.
     *
     * @param channel the channel identifier required by the back-end system (@PORTAL, @WEB, @ESB...).
     */
    public DialectZos(final String channel) {
        this.channel = channel;

    }


    @Override
    public void formatToJmsText(final DtoJmsContext jmsContext, final DtoJmsRequest request) {
        final DtoContext dtoContext = jmsContext.getDtoContext();
        final DtoFormatter formatter = dtoContext.getDtoFormatter();

        // format DTO
        final String rawData = formatter.format(request.getRequestDto());

        // insert header prefix
        String rawJms = formatWithHeader(request, rawData);

        request.setRawRequest(rawJms);
    }

    @Override
    public void parseFromJmsText(final DtoJmsContext jmsContext, final DtoJmsRequest request) {

    }


    private String formatWithHeader(final DtoJmsRequest request,
                                    final String data) {

        final String userId = request.getUserId();
        final String userProfile = request.getUserProfile();
        final String sessionId = request.getSessionId();
        final String computerName = request.getUserComputerName();
        final String jmsName = request.getDtoRequestAnnotation().name();

        // person number can be null. if null it will be filled with spaces.
        String personNumber = request.getPersonNumber();
        if (personNumber != null) {
            personNumber = DtoUtils.alignRight(personNumber, 8, '0');
        }

        if ((channel == null) || (userId == null) || (userProfile == null) || (jmsName == null)) {
            throw new IllegalArgumentException("all the mandatory parameters are not set: channel ["
                    + channel + "] userId [" + userId + "] userProfile [" + userProfile
                    + "] jmsName [" + jmsName + "]");
        }

        // Transaction's sbHeader
        final StringBuilder sbHeader = new StringBuilder(64);
        // RCV-HRD-FLAGS-FIL     PIC X(8)
        sbHeader.append("000");
        sbHeader.append(false ? 'V' : '0'); // isVerboseMode ?
        // '0' or 'S': Safe    'U': Unsafe (fraud detection)
        sbHeader.append(request.isUnsafe() ? 'U' : 'S');
        sbHeader.append("000");

        // RCV-HDR-SESSION (36) 8 + 8 + 20 || 8 + 12 + 4 + 12
        sbHeader.append(DtoUtils.alignLeft(userId, 8));              // RCV-HRD-USER-ID       PIC X(8)
        if (personNumber != null) {
            sbHeader.append(DtoUtils.alignLeft(personNumber, 8));    // RCV-HRD-NU-PERSONNE    PIC X(8)
            sbHeader.append(DtoUtils.alignLeft(null, 20));           // RCV-HRD-SESSION-FILLER PIC X(20)
        } else {
            sbHeader.append(DtoUtils.alignLeft(userProfile, 12));    // RCV-HRD-PROFIL        PIC X(12)
            sbHeader.append(DtoUtils.alignLeft(sessionId, 4));       // RCV-HRD-SESSION-ID    PIC X(4)
            sbHeader.append(DtoUtils.alignLeft(computerName, 12));   // RCV-HRD-COMPUTER-NAME PIC X(12)
        }

        // Transaction's sbRequest
        final StringBuilder sbRequest = new StringBuilder(data.length() + 32);
        sbRequest.append(DtoUtils.alignLeft(jmsName, 8));         // RCV-REQ-NAME          PIC X(8)
        sbRequest.append(DtoUtils.alignRight(data.length(), 7));  // RCV-REQ-LGT           PIC 9(7)
        sbRequest.append(data);                                   // RCV-REQ-DATA          PIC X(24493)

        // person number + sbHeader + sbRequest
        final StringBuilder sbTransactionContent = new StringBuilder(sbHeader.length() + sbRequest.length() + 32);
        sbTransactionContent.append(DtoUtils.alignLeft("HEADER", 8));           // RCV-HDR-NAME              PIC X(8)
        sbTransactionContent.append(DtoUtils.alignRight(sbHeader.length(), 7)); // RCV-HDR-LGT               PIC 9(7)
        sbTransactionContent.append(sbHeader);
        sbTransactionContent.append(sbRequest);

        final StringBuilder sbTransaction = new StringBuilder(sbTransactionContent.length() + 32);
        sbTransaction.append(DtoUtils.alignLeft(channel, 8));                        // RCV-TRX-NAME              PIC X(8)
        sbTransaction.append(DtoUtils.alignRight(sbTransactionContent.length(), 7)); // RCV-TRX-LGT               PIC 9(7)
        sbTransaction.append(sbTransactionContent);

        return sbTransaction.toString();

    }
}
