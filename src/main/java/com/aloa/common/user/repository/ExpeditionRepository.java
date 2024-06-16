package com.aloa.common.user.repository;

import com.aloa.common.user.entitiy.Expedition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpeditionRepository extends JpaRepository<Expedition, Long> {
    List<Expedition> findByNameStartsWith(String name);
    List<Expedition> findByChosungStartsWith(String chosung);
    List<Expedition> findByUserId(Long userId);
}
