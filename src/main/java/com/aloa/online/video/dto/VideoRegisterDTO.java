package com.aloa.online.video.dto;

import com.aloa.common.user.entitiy.primarykey.LostArkCharacterPK;
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
    private LostArkCharacterPK characterId;
}
