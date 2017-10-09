package com.sbroussi.dto.jms;


import com.sbroussi.dto.DtoContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import javax.jms.Queue;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class DtoJmsContext {

    @NonNull
    private DtoContext dtoContext;

    /**
     * The ID of te running application. This ID will be checked against 'DtoRequest.usedByApplications()'.
     */
    @NonNull
    private String applicationId;

    /**
     * The JMS queue where to PUT the request.
     */
    @NonNull
    private Queue requestQueue;

    /**
     * The JMS queue where to READ the responses ('null' for ONE-WAY request).
     */
    private Queue replyQueue;

    @NonNull
    private MessageSender messageSender;

    /**
     * A chain of auditors: they will be called in the order of the list.
     * <p>
     * The auditors are called:
     * <p>
     * - before sending the JMS request
     * <p>
     * - after receiving the JMS response (or when the One-Way request has been sent).
     */
    @Builder.Default
    private List<DtoJmsAudit> dtoJmsAuditors = buildDefaultAuditors();

    /**
     * @return the list of default auditors.
     */
    private static List<DtoJmsAudit> buildDefaultAuditors() {
        ArrayList<DtoJmsAudit> list = new ArrayList<DtoJmsAudit>();
        list.add(new DtoJmsAuditLogger());
        return list;
    }

}
