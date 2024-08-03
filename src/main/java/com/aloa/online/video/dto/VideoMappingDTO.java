package com.aloa.online.video.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VideoMappingDTO {
    @NotNull(message = "비디오ID는 필수값입니다.")
    private Long videoId;
    @NotNull(message = "캐릭터ID는 필수입니다.")
    private LostArkCharacterIdDTO lostArkCharacterIdDTO;
}
