package com.sbroussi.dto.jms;


import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.jms.audit.Auditor;
import com.sbroussi.dto.jms.audit.AuditorLogger;
import com.sbroussi.dto.jms.dialect.Dialect;
import com.sbroussi.dto.transport.MessageSender;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
     * The 'dialect' defines how the JMS messages must be formatted or parsed.
     */
    @NonNull
    private Dialect dialect;

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
    private List<Auditor> dtoJmsAuditors = buildDefaultAuditors();

    /**
     * @return the list of default auditors.
     */
    private static List<Auditor> buildDefaultAuditors() {
        ArrayList<Auditor> list = new ArrayList<Auditor>();
        list.add(new AuditorLogger());
        return list;
    }

}