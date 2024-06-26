package com.aloa.common.video.entity;

import com.aloa.common.card.entity.Engrave;
import com.aloa.common.user.entitiy.LostArkCharacter;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;
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

    @Builder
    public Video(String description, String path, String youtubeVideoId, String chosung, String title, Long id, String clientVersion) {
        this.description = description;
        this.path = path;
        this.youtubeVideoId = youtubeVideoId;
        this.chosung = chosung;
        this.title = title;
        this.id = id;
        this.clientVersion = clientVersion;
    }

    public void mapCharacter(@NonNull @Valid LostArkCharacter lostArkCharacter) {
        this.expeditionId = lostArkCharacter.getExpeditionId();
        this.characterSequence = lostArkCharacter.getSequence();
    }

    public void setCalculationState(@NonNull @Valid CalculationState calculationState) {
        this.calculationState = calculationState;
    }

    public void setClientVersion(@NonNull @Valid String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public void setEngrave(@NonNull @Valid Engrave engrave) {
        this.engrave = engrave;
    }
}
