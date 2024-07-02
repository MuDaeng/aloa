package com.aloa.common.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Token {
    @Column(nullable = false)
    @Id
    private String refreshToken;

    @Column(nullable = false)
    private String accessToken;
}
