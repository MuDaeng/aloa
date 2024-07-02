package com.aloa.common.video.entity;

import com.aloa.common.card.entity.Engrave;
import com.aloa.common.video.entity.primarykey.VideoHistPK;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@IdClass(VideoHistPK.class)
@Entity
public class VideoHist {
    @Id
    private Long id;
    @Id
    private Integer histSequence;
    @Column(nullable = false)
    private String title;
    /** 제목의 초성 검색용 */
    @Column(nullable = false)
    private String chosung;
    @Column(nullable = false, length = 500)
    private String path;

    @Column(nullable = false)
    private String youtubeVideoId;

    @Column(length = 1500)
    private String description;

    private Long expeditionId;
    private Integer characterSequence;

    private String clientVersion;

    @Enumerated(EnumType.ORDINAL)
    private Engrave engrave;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private CalculationState calculationState;

    private LocalDateTime regTime;

    public VideoHist(@NonNull @Valid Video video, Integer histSequence){
        this.id = video.getId();
        this.histSequence = histSequence;
        this.title = video.getTitle();
        this.chosung = video.getChosung();
        this.path = video.getPath();
        this.youtubeVideoId = video.getYoutubeVideoId();
        this.description = video.getDescription();
        this.expeditionId = video.getExpeditionId();
        this.characterSequence = video.getCharacterSequence();
        this.clientVersion = video.getClientVersion();
        this.engrave = video.getEngrave();
        this.calculationState = video.getCalculationState();
        this.regTime = LocalDateTime.now();
    }
}
