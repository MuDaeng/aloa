package com.aloa.online.video.service;

import com.aloa.common.user.entitiy.LostArkCharacter;
import com.aloa.common.user.repository.LostArkCharacterRepository;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.common.video.handler.VideoFinder;
import com.aloa.online.video.dto.VideoInquiryDTO;
import com.aloa.online.video.mapper.VideoInquiryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class VideoInquiryService {
    private final VideoFinder videoFinder;
    private final LostArkCharacterRepository lostArkCharacterRepository;

    @Transactional(readOnly = true)
    public VideoInquiryDTO findByVideoId(@RequestParam Long videoId) {
        List<VideoCalculationResult> videoCalculationResultList = videoFinder.findCalculationResultByVideoId(videoId);
        var videoMapping = videoFinder.findMappingByVideoId(videoId);
        var path = videoFinder.findById(videoId).map(Video::getPath).orElse(null);
        var characterName = videoMapping
                .map(mapping -> lostArkCharacterRepository.findByExpeditionIdAndSequence(mapping.getExpeditionId(), mapping.getSequence()))
                .map(lostArkCharacter -> lostArkCharacter.orElse(null))
                .map(LostArkCharacter::getNickName)
                .orElse(null);
        Predicate<VideoCalculationResult> isIndependentTrials = VideoCalculationResult::isIndependentTrials;

        var dependentTrials = videoCalculationResultList.stream().filter(isIndependentTrials.negate()).toList();
        var independentTrials = videoCalculationResultList.stream().filter(isIndependentTrials).toList();

        return VideoInquiryDTO.builder()
                .characterName(characterName)
                .path(path)
                .independentTrials(VideoInquiryMapper.INSTANCE.toCardCntDTOList(independentTrials))
                .cardList(VideoInquiryMapper.INSTANCE.toCardCntDTOList(dependentTrials))
                .build();
    }
}
