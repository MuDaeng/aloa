package com.aloa.common.user.entitiy;

import com.aloa.common.user.entitiy.primarykey.UserRole;
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
public class User {
    /** 사용자 pk */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String name;
    /** 구글사용자 이메일 */
    @Column(unique=true, nullable = false, updatable = false)
    private String googleUserId;

    @OneToMany(mappedBy = "userId")
    private List<Expedition> expeditionList;
}
