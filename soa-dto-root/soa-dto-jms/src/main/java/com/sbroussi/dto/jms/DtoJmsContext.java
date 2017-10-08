package com.sbroussi.dto.jms;


import com.sbroussi.dto.DtoContext;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

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
     * A chain of auditor: they will be called in the order of the list.
     */
    @Builder.Default
    private List<DtoJmsAudit> dtoJmsAuditors = buildDefaultAuditors();

    /**
     * @return the list of default autitors.
     */
    private static List<DtoJmsAudit> buildDefaultAuditors() {
        ArrayList<DtoJmsAudit> list = new ArrayList<DtoJmsAudit>();
        list.add(new DtoJmsAuditLogger());
        return list;
    }

}
