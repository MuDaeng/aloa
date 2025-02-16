package com.aloa.common.statistics.entity;

import com.aloa.common.video.entity.Video;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class CalculationReservedVideo {
    @NotNull
    @Id
    private Long videoId;

    CalculationReservedVideo(Video video){
        this.videoId = video.getId();
    }
}
