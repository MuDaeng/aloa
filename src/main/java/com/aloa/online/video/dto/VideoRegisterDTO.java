package com.aloa.online.video.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class VideoRegisterDTO {
    @NotBlank(message = "경로는 필수")
    private String path;
    private String engrave;
    private String sideNode;
    private LostArkCharacterIdDTO characterId;
}
