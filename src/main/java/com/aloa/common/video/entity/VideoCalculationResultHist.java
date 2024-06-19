package com.aloa.common.video.entity;

import com.aloa.common.card.entity.Card;
import com.aloa.common.video.entity.primarykey.VideoCalculationResultHistPK;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@IdClass(VideoCalculationResultHistPK.class)
public class VideoCalculationResultHist {
    @Id
    private Long videoId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sequence;
    @Id
    @Enumerated(EnumType.ORDINAL)
    private Card card;

    @Column(nullable = false)
    private Integer cnt;

    @Id
    @Column(nullable = false)
    private boolean independentTrials;

    @Column(nullable = false)
    private LocalDateTime regTime;

    @Builder
    public VideoCalculationResultHist(@NonNull @Valid VideoCalculationResult videoCalculationResult) {
        this.videoId = videoCalculationResult.getVideoId();
        this.card = videoCalculationResult.getCard();
        this.cnt = videoCalculationResult.getCnt();
        this.independentTrials = videoCalculationResult.isIndependentTrials();
        this.regTime = LocalDateTime.now();
    }
}
