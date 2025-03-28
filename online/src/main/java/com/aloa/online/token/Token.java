package com.aloa.online.token;

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

    @Setter
    @Column(nullable = false)
    private String accessToken;
}
