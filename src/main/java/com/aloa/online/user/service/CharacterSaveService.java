package com.aloa.online.user.service;

import com.aloa.common.user.entitiy.Expedition;
import com.aloa.common.user.entitiy.LostArkCharacter;
import com.aloa.common.user.repository.ExpeditionRepository;
import com.aloa.common.user.repository.LostArkCharacterRepository;
import com.aloa.common.user.repository.UserRepository;
import com.aloa.common.util.ChosungExtractor;
import com.aloa.common.video.feignclient.LostArkFeignClient;
import com.aloa.common.video.feignclient.vo.CharacterProfile;
import com.aloa.online.user.dto.CharacterMappingDTO;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterSaveService {
    private final UserRepository userRepository;
    private final LostArkCharacterRepository lostArkCharacterRepository;
    private final ExpeditionRepository expeditionRepository;

    private final LostArkFeignClient lostArkFeignClient;

    @Transactional
    public void mapCharacter(@NonNull @Valid CharacterMappingDTO character) {
        var user = userRepository.findById(character.getApplicantId()).orElseThrow(() -> new IllegalArgumentException("User not found"));

        var characterProfileList = lostArkFeignClient.retrieveAllCharacter(character.getCharacterName());

        var inputtedCharacter = Optional.ofNullable(characterProfileList).stream()
                .flatMap(List::stream)
                .filter(characterProfile -> character.getCharacterName().equals(characterProfile.CharacterName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Character not found"));

        var arcanaList = Optional.of(characterProfileList).stream()
                .flatMap(List::stream)
                .filter(characterProfile -> "아르카나".equals(characterProfile.CharacterClassName()))
                .filter(characterProfile -> inputtedCharacter.ServerName().equals(characterProfile.ServerName()))
                .map(characterProfile -> lostArkFeignClient.retrieveCharacterProfile(characterProfile.CharacterName()))
                .toList();

        if(arcanaList.isEmpty()) {
            throw new IllegalArgumentException("Character not found");
        }

        var expeditionName = arcanaList.stream()
                .map(CharacterProfile::TownName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("invalid expedition name"));

        var expedition = expeditionRepository.findByName(expeditionName)
                .orElse(expeditionRepository.save(Expedition.builder()
                .name(expeditionName)
                .chosung(ChosungExtractor.extractChosung(expeditionName))
                .userId(character.getApplicantId())
                .build()));

        var expeditionCharacters = lostArkCharacterRepository.findByExpeditionId(expedition.getId()).stream().collect(Collectors.toMap(
                LostArkCharacter::getNickName, value -> value
        ));

        var maxSequence = expeditionCharacters.values().stream().map(LostArkCharacter::getSequence).max(Integer::compareTo).orElse(1);

        var lostArkCharacterList = new ArrayList<LostArkCharacter>();

        for(var arcana : arcanaList) {
            var sequence = Optional.ofNullable(expeditionCharacters.get(arcana.CharacterName())).map(LostArkCharacter::getSequence).orElse(maxSequence++);
            lostArkCharacterList.add(
                    LostArkCharacter.builder()
                    .expeditionId(expedition.getId())
                    .sequence(sequence)
                    .nickName(arcana.CharacterName())
                    .chosung(ChosungExtractor.extractChosung(arcana.CharacterName()))
                    .imageUrl(arcana.CharacterImage())
                    .arcana(true)
                    .deleted(false)
                    .build()
            );
        }


        lostArkCharacterRepository.saveAll(lostArkCharacterList);
    }
}
