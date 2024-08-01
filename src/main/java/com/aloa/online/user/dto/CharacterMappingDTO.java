package com.aloa.online.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterMappingDTO {
    @NotBlank
    private Long applicantId;
    @NotBlank
    private String characterName;
}
