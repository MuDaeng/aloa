package com.aloa.common.video.entity;

import com.aloa.common.card.entity.Engrave;
import com.aloa.common.card.entity.PredicateKnight;
import com.aloa.common.card.entity.SideNode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class Video implements PredicateKnight {
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

    @Column(nullable = false)
    private String youtubeVideoId;

    @Column(length = 1500)
    private String description;

    private String clientVersion;

    @Enumerated(EnumType.ORDINAL)
    @Setter
    private Engrave engrave;

    @Enumerated(EnumType.ORDINAL)
    @Setter
    private SideNode sideNode;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    @Setter
    private CalculationState calculationState;

    @Builder
    public Video(String description, String path, String youtubeVideoId, String chosung, String title, Long id, String clientVersion, SideNode sideNode) {
        this.description = description;
        this.path = path;
        this.youtubeVideoId = youtubeVideoId;
        this.chosung = chosung;
        this.title = title;
        this.id = id;
        this.clientVersion = clientVersion;
        this.sideNode = sideNode;
    }

    @Override
    public boolean isKnight(){
        return SideNode.KNIGHT.equals(sideNode);
    }
}
