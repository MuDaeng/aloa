package com.aloa.common.video.handler;

import com.aloa.common.card.entity.Card;
import com.aloa.common.util.CropVideoTotal;
import com.aloa.common.util.VideoFileUtils;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.common.video.manager.VideoSaveManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Transactional
public class VideoCalculator {

    private final VideoSaveManager videoSaveManager;
    private final VideoValidator videoValidator;
    private final VideoFileUtils videoFileUtils;

    public void calculate(@NonNull Video video) {

        videoSaveManager.notifyCalculating(video);

        var ocrResult = getOcrResult(video);

        var zCardList = getCardLifePeriodList(ocrResult.zList());
        var xCardList = getCardLifePeriodList(ocrResult.xList());

        var independentTrialsCardList = getCardLifePeriodListIndependentTrials(zCardList, xCardList);

        //독립시행 종속시행 포함
        var videoCalculationResultList = toVideoCalculationResultList(zCardList, xCardList, independentTrialsCardList, video);

        videoSaveManager.regCalculationResult(videoCalculationResultList);

        videoSaveManager.nofityCompleted(video);
    }

    private OcrResult getOcrResult(Video video) {
        var videoFileName = videoValidator.extractVideoId(video.getPath());

        final CropVideoTotal cropVideoTotal;

        try {
            cropVideoTotal = videoFileUtils.cropVideo(videoFileName);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        var cardImageVideoList = List.of(cropVideoTotal.zImage(), cropVideoTotal.xImage());
        var cardNameVideoList = List.of(cropVideoTotal.zName(), cropVideoTotal.xName());

        Stream.of(cardImageVideoList, cardNameVideoList)
            .flatMap(List::stream)
            .forEach(videoFileUtils::extractFrames);

        videoFileUtils.preprocessOcrImage(cardNameVideoList);

        return videoFileUtils.getOcrImageResult(cardNameVideoList);
    }

    private List<CardLifePeriod> getCardLifePeriodList(List<String> ocrCardList) {
        var cardLifePeriodList = new ArrayList<CardLifePeriod>();
        for(var i = 0; i < ocrCardList.size(); i++) {

        }
        return cardLifePeriodList;
    }

    private List<CardLifePeriod> getCardLifePeriodListIndependentTrials(List<CardLifePeriod> zCardList, List<CardLifePeriod> xCardList) {
        var cardLifePeriodList = new ArrayList<CardLifePeriod>();
        return cardLifePeriodList;
    }

    private List<VideoCalculationResult> toVideoCalculationResultList(List<CardLifePeriod> zCardList, List<CardLifePeriod> xCardList,
                                                                      List<CardLifePeriod> independentTrialsCardList, Video video) {
        var cardCount = Stream.of(zCardList, xCardList)
                .flatMap(List::stream)
                .collect(
                        Collectors.groupingBy(CardLifePeriod::card, Collectors.counting())
                );

        var independentCardCount = independentTrialsCardList.stream()
                .collect(
                        Collectors.groupingBy(CardLifePeriod::card, Collectors.counting())
                );

        var cardTotal = cardCount.entrySet().stream()
                .map(entry -> new VideoCalculationResult(video, entry.getKey(), entry.getValue().intValue(), false))
                .toList();

        var independentTrials = independentCardCount.entrySet().stream()
                .map(entry -> new VideoCalculationResult(video, entry.getKey(), entry.getValue().intValue(), true))
                .toList();

        return Stream.of(cardTotal, independentTrials).flatMap(List::stream).toList();
    }

    private Card getCardForString(String cardString) {
        var validCardStrings = getValidCardMap();

        var card = validCardStrings.get(cardString);

        if(card == null) card = Card.EMPTY;

        return card;
    }

    private Map<String, Card> getValidCardMap() {
        var validCardMap = new HashMap<String, Card>();
        validCardMap.put("", Card.달);
        return validCardMap;
    }
}