package com.aloa.common.user.repository;

import com.aloa.common.user.entitiy.LostArkCharacter;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LostArkCharacterRepository extends JpaRepository<LostArkCharacter, Long> {
    List<LostArkCharacter> findByExpeditionId(@NonNull Long expeditionId);
    List<LostArkCharacter> findByNickNameStartsWith(@NonNull String nickName);
    List<LostArkCharacter> findByChosungStartsWith(@NonNull String chosung);

    List<LostArkCharacter> findByExpeditionIdAndIsArcana(@NonNull Long expeditionId, Boolean isArcana);
    LostArkCharacter findByExpeditionIdAndSequence(@NonNull Long expeditionId, @NonNull Integer sequence);
}
