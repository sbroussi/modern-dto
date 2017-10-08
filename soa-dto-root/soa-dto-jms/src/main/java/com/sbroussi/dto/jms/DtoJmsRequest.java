package com.sbroussi.dto.jms;


import com.sbroussi.dto.annotations.DtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoJmsRequest {

    /**
     * The request DTO,
     */
    private final Object requestDto;

    /**
     * The '@DtoRequest' annotation of the request DTO.
     */
    private DtoRequest dtoRequestAnnotation;

    private String rawMessage;

    public DtoJmsRequest(final Object requestDto) {
        this.requestDto = requestDto;
    }

}
