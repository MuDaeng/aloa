package com.aloa.common.video.entity;

import com.aloa.common.user.entitiy.LostArkCharacter;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    /** 제목의 초성 검색용 */
    @Column(nullable = false)
    private String chosung;
    @Column(nullable = false, length = 500)
    private String path;

    @Column(length = 1500)
    private String description;

    private Long expeditionId;
    private Integer characterSequence;

    private String clientVersion;

    @Enumerated(EnumType.ORDINAL)
    private Engrave engrave;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private CalculationStateCode calculationStateCode;

    public void mapCharacter(@NonNull LostArkCharacter lostArkCharacter) {

    }
}
