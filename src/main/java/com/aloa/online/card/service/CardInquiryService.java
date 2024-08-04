package com.aloa.online.card.service;

import com.aloa.common.user.entitiy.LostArkCharacter;
import com.aloa.common.user.repository.LostArkCharacterRepository;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.common.video.handler.VideoFinder;
import com.aloa.online.card.dto.CardCntDTO;
import com.aloa.online.card.dto.CharacterInquiryDTO;
import com.aloa.online.card.dto.VideoInquiryDTO;
import com.aloa.online.card.mapper.CardInquiryMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardInquiryService {
    private final VideoFinder videoFinder;
    private final LostArkCharacterRepository lostArkCharacterRepository;

    @Transactional(readOnly = true)
    public VideoInquiryDTO findByVideoId(Long videoId) {
        List<VideoCalculationResult> videoCalculationResultList = videoFinder.findCalculationResultByVideoId(videoId);
        var videoMapping = videoFinder.findMappingByVideoId(videoId);
        var video = videoFinder.findById(videoId);
        var path = video.map(Video::getPath).orElse(null);
        var engrave = video.map(Video::getEngrave).orElse(null);
        var sideNode = video.map(Video::getSideNode).orElse(null);
        var characterName = videoMapping
                .map(mapping -> lostArkCharacterRepository.findByExpeditionIdAndSequence(mapping.getExpeditionId(), mapping.getSequence()))
                .flatMap(Function.identity())
                .map(LostArkCharacter::getNickName)
                .orElse(null);
        Predicate<VideoCalculationResult> isIndependentTrials = VideoCalculationResult::isIndependentTrials;

        var dependentTrials = videoCalculationResultList.stream().filter(isIndependentTrials.negate()).toList();
        var independentTrials = videoCalculationResultList.stream().filter(isIndependentTrials).toList();

        return VideoInquiryDTO.builder()
                .characterName(characterName)
                .path(path)
                .sideNode(sideNode != null ? sideNode.name() : null)
                .engrave(engrave != null ? engrave.name() : null)
                .independentTrials(CardInquiryMapper.INSTANCE.toCardCntDTOList(independentTrials))
                .cardList(CardInquiryMapper.INSTANCE.toCardCntDTOList(dependentTrials))
                .build();
    }

    @Transactional(readOnly = true)
    public CharacterInquiryDTO findByCharacterId(@NonNull Long expeditionId, @NonNull Integer characterId) {
        var characterName = lostArkCharacterRepository.findByExpeditionIdAndSequence(expeditionId, characterId)
                .map(LostArkCharacter::getNickName)
                .orElseThrow(() -> new IllegalArgumentException("Character not found"));

        var cardCounts = videoFinder.findCalculationResultByCharacter(expeditionId, characterId)
                .stream()
                .collect(
                        Collectors.groupingBy(
                                VideoCalculationResult::getCard, Collectors.summingLong(VideoCalculationResult::getCnt)
                        ))
                .entrySet()
                .stream()
                .map(entry ->
                        CardCntDTO.builder()
                                .card(entry.getKey())
                                .cnt(entry.getValue())
                                .build()
                )
                .toList();
        return CharacterInquiryDTO.builder()
                .characterName(characterName)
                .cardList(cardCounts)
                .build();
    }
}
