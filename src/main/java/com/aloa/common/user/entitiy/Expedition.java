package com.aloa.common.user.entitiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Expedition {
    /** 원정대 pk */
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** 사용자 ID 외래키 */
    @Column(nullable = false)
    private Long userId;

    /** 원정대 이름 */
    @Column(nullable = false)
    private String name;
    /** 원정대 초성 초성검색용 */
    @Column(nullable = false)
    private String chosung;

    @OneToMany(mappedBy = "expeditionId")
    private List<LostArkCharacter> lostArkCharacterList;

    public void mapLostArkCharacters(List<LostArkCharacter> lostArkCharacters){
        this.lostArkCharacterList = lostArkCharacters;
    }
}
