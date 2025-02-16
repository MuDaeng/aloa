package com.aloa.common.user.validator;

import com.aloa.common.user.entitiy.LostArkCharacter;
import com.aloa.common.user.repository.LostArkCharacterRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CharacterValidator {
    private final LostArkCharacterRepository lostArkCharacterRepository;

    public LostArkCharacter findCharacter(@NonNull @Valid LostArkCharacter lostArkCharacter) {
        var character = lostArkCharacterRepository.findByExpeditionIdAndSequence(lostArkCharacter.getExpeditionId(), lostArkCharacter.getSequence());

        return character.orElse(null);
    }
}
