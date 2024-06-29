package com.aloa.configuration.oauth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponseDTO {
    private Integer status;
    private Object Data;

    public static StatusResponseDTO addStatus(Integer status) {
        return StatusResponseDTO.builder().status(status).build();
    }

    public static StatusResponseDTO success() {
        return StatusResponseDTO.builder().status(200).build();
    }

    public static StatusResponseDTO success(Object data) {
        return StatusResponseDTO.builder().status(200).Data(data).build();
    }
}
