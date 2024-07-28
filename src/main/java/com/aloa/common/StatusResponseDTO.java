package com.aloa.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponseDTO {
    private Integer status;
    private Object Data;

    public static StatusResponseDTO addStatus(Integer status) {
        return new StatusResponseDTO(status, null);
    }

    public static StatusResponseDTO addStatus(Integer status, Object data) {
        return new StatusResponseDTO(status, data);
    }

    public static StatusResponseDTO success() {
        return new StatusResponseDTO(200, null);
    }

    public static StatusResponseDTO success(Object data) {
        return new StatusResponseDTO(200, data);
    }
}
