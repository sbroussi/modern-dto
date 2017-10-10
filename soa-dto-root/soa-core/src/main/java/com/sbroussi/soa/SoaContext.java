package com.sbroussi.soa;


import com.sbroussi.dto.DtoContext;
import com.sbroussi.dto.transport.MessageSender;
import com.sbroussi.soa.audit.Auditor;
import com.sbroussi.soa.audit.AuditorLogger;
import com.sbroussi.soa.dialect.Dialect;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class SoaContext {

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
     * - Before the JMS REQUEST is sent.
     * <p>
     * - After the JMS REQUEST is sent and the RAW JMS RESPONSE is read (if any).
     * <p>
     * - After the RAW JMS RESPONSE has been parsed and Response Objects
     * are populated in the 'SoaDtoResponse' (if any).
     */
    @Builder.Default
    private List<Auditor> auditors = buildDefaultAuditors();

    /**
     * @return the list of default auditors.
     */
    private static List<Auditor> buildDefaultAuditors() {
        ArrayList<Auditor> list = new ArrayList<Auditor>();
        list.add(new AuditorLogger());
        return list;
    }

}
