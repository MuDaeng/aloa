package com.aloa.common.user.entitiy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class User {
    /** 사용자 pk */
    @Id
    private Long id;
    /** 구글인증토큰 */
    @Column(unique=true, nullable = false, updatable = false)
    private String googleAuthToken;
    /** 구글사용자 이메일 */
    @Column(unique=true, nullable = false, updatable = false)
    private Long googleUserId;

    @OneToMany(mappedBy = "userId")
    private List<Expedition> expeditionList;
}
