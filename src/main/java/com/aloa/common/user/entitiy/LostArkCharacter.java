package com.aloa.common.user.entitiy;

import com.aloa.common.video.entity.Video;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@IdClass(LostArkCharacterPK.class)
public class LostArkCharacter {
    /** 원정대 ID */
    @Id
    @Column(name = "expedition_id", nullable = false)
    private Long expeditionId;
    /** 원정대의 캐릭터 순번 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sequence;
    /** 닉네임 */
    @Column(nullable = false)
    private String nickName;
    /** 닉네임 초성 초성검색용 */
    @Column(nullable = false)
    private String chosung;
    /** 아르카나여부 확인용 아르카나가 아니면 비디오 계산결과를 캐릭터에 매핑 불가 */
    @Column(nullable = false)
    private boolean isArcana;
    /** 삭제여부 체크 */
    @Column(nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    private Expedition expedition;

    @OneToMany
    @JoinColumn(name="expeditionId")
    @JoinColumn(name="characterSequence")
    private List<Video> videoList;
}
