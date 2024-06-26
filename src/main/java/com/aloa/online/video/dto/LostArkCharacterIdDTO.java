package com.aloa.online.video.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LostArkCharacterIdDTO {
    @NotNull
    private Long expeditionId;

    @NotNull
    private int sequence;
}
