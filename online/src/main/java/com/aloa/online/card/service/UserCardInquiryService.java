package com.aloa.online.card.service;

import com.aloa.common.card.dto.CardCnt;
import com.aloa.common.card.entity.SideNode;
import com.aloa.common.user.entitiy.LostArkCharacter;
import com.aloa.common.user.handler.UserFinder;
import com.aloa.common.user.repository.LostArkCharacterRepository;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.common.video.handler.VideoFinder;
import com.aloa.online.card.dto.CardConditionSumInquiryDTO;
import com.aloa.online.card.dto.CharacterInquiryDTO;
import com.aloa.online.card.dto.VideoInquiryDTO;
import com.aloa.online.card.mapper.UserCardInquiryMapper;
import com.aloa.online.configuration.security.oauth.SignedInUserFinder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserCardInquiryService {
    private final VideoFinder videoFinder;
    private final LostArkCharacterRepository lostArkCharacterRepository;
    private final UserFinder userFinder;

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
                .independentTrials(UserCardInquiryMapper.INSTANCE.toCardCntDTOList(independentTrials))
                .cardList(UserCardInquiryMapper.INSTANCE.toCardCntDTOList(dependentTrials))
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
                .map(entry -> new CardCnt(entry.getKey(), entry.getValue())
                )
                .toList();
        return CharacterInquiryDTO.builder()
                .characterName(characterName)
                .cardList(cardCounts)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CardConditionSumInquiryDTO> findByUserId() {
        var userId = SignedInUserFinder.getSignedInUser().getUserId();
        var user = userFinder.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var userCalculations =  videoFinder.findCalculationResultByGoogleUserId(user.getGoogleUserId());

        var videoIds = userCalculations.stream().map(VideoCalculationResult::getVideoId).collect(Collectors.toSet());

        var userVideos = videoFinder.findAllById(videoIds);

        var videosGroupingByEngrave = userVideos.stream().collect(Collectors.groupingBy(Video::getEngrave));

        var cardConditionSumList = new ArrayList<CardConditionSumInquiryDTO>();

        for(var videoEntry : videosGroupingByEngrave.entrySet()){
            var engrave = videoEntry.getKey();
            var videos = videoEntry.getValue();

            Predicate<VideoCalculationResult> isIndependentTrials = VideoCalculationResult::isIndependentTrials;

            Function<Video, Long> mapId = Video::getId;

            var idsList = Stream.of(SideNode.values()).collect(
                    Collectors.toMap(sideNode -> sideNode, sideNode -> this.mapList(sideNode, videos, mapId))
            );

            for(var idsEntry : idsList.entrySet()){
                var sideNode = idsEntry.getKey();
                var ids = idsEntry.getValue();

                var totalCardCount = getCardCnt(userCalculations, ids, Predicate.not(isIndependentTrials));
                var independentTrialsCardCount = getCardCnt(userCalculations, ids, isIndependentTrials);

                CardConditionSumInquiryDTO totalCardSum = CardConditionSumInquiryDTO.builder()
                        .engrave(engrave.getName())
                        .sideNode(sideNode.name())
                        .isIndependentTrials(false)
                        .cardCntList(totalCardCount)
                        .build();

                CardConditionSumInquiryDTO independentTrialCardSum = CardConditionSumInquiryDTO.builder()
                        .engrave(engrave.getName())
                        .sideNode(sideNode.name())
                        .isIndependentTrials(true)
                        .cardCntList(independentTrialsCardCount)
                        .build();

                cardConditionSumList.add(totalCardSum);
                cardConditionSumList.add(independentTrialCardSum);
            }
        }

        return cardConditionSumList;
    }

    private List<CardCnt> getCardCnt(List<VideoCalculationResult> userCalculations, List<Long> videoIds, Predicate<VideoCalculationResult> predicateIndependentTrials) {
        var card = userCalculations.stream()
                .filter(userCalculation -> videoIds.contains(userCalculation.getVideoId()))
                .filter(predicateIndependentTrials)
                .collect(Collectors.groupingBy(VideoCalculationResult::getCard, Collectors.summingLong(VideoCalculationResult::getCnt)));

        return card.entrySet().stream()
                .map(cardEntry -> new CardCnt(cardEntry.getKey(), cardEntry.getValue()))
                .sorted(Comparator.comparing(CardCnt::cnt).reversed())
                .toList();
    }

    private List<Long> mapList(SideNode sideNode, List<Video> predicateKnights, Function<Video, Long> mapper) {
        return predicateKnights.stream()
                .filter(sideNode.getPredicate())
                .map(mapper)
                .toList();
    }
}
