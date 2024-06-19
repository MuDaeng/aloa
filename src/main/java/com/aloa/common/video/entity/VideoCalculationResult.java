package com.aloa.common.video.entity;

import com.aloa.common.card.entity.Card;
import com.aloa.common.video.entity.primarykey.VideoCalculationResultPK;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@IdClass(VideoCalculationResultPK.class)
public class VideoCalculationResult {
    @Id
    private Long videoId;
    @Enumerated(EnumType.ORDINAL)
    @Id
    private Card card;
    /** 카드 갯수 */
    @Column(nullable = false)
    private int cnt;
    @Id
    private boolean independentTrials;

    @Builder
    public VideoCalculationResult(@Valid Video video, Card card, boolean independentTrials) {
        this.videoId = video.getId();
        this.card = card;
        this.independentTrials = independentTrials;
    }

    public void incrementCnt() {
        this.cnt++;
    }
}
