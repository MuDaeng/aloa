package com.aloa.common.client.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ClientVersion {
    @Id
    private String version;
    @Column(nullable = false)
    private LocalDate updateDate;
    @Column(nullable = false)
    private boolean isOfficialBalancePatch;
}
