package com.aloa.common.video.handler;

import com.aloa.common.card.entity.Card;
import com.aloa.common.card.entity.Engrave;
import com.aloa.common.statistics.entity.ReservedVideoList;
import com.aloa.common.statistics.repository.CalculationReservedVideoRepository;
import com.aloa.common.util.CropVideoTotal;
import com.aloa.common.util.VideoFileUtils;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.common.video.manager.VideoSaveManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class VideoCalculator {

    private final VideoSaveManager videoSaveManager;
    private final VideoFinder videoFinder;
    private final CalculationReservedVideoRepository calculationReservedVideoRepository;;
    private final VideoValidator videoValidator;
    private final VideoFileUtils videoFileUtils;

    public void calculate(@NonNull Video video) {

        videoSaveManager.notifyCalculating(video);

        var ocrResult = getOcrResult(video);

        var zCardList = getCardLifePeriodList(toCardEnumList(ocrResult.zList()));
        var xCardList = getCardLifePeriodList(toCardEnumList(ocrResult.xList()));

        log.debug("zCardList: {}, xCardList: {}", zCardList, xCardList);

        var independentTrialsCardList = getCardLifePeriodListIndependentTrials(zCardList, xCardList);

        log.debug("independentTrialsCardList: {}", independentTrialsCardList);

        //독립시행 종속시행 포함
        var videoCalculationResultList = toVideoCalculationResultList(zCardList, xCardList, independentTrialsCardList, video);

        videoCalculationResultList = videoCalculationResultList.stream().filter(calculationResult -> !calculationResult.getCard().equals(Card.EMPTY)).toList();

        log.debug("videoCalculationResultList: {}", videoCalculationResultList);

        videoSaveManager.regCalculationResult(videoCalculationResultList);

        var lastVersionVideo = videoFinder.findById(video.getId()).orElse(video);

        videoCalculationResultList.stream()
                .map(videoCalculationResult -> videoCalculationResult.getCard().getEngrave())
                .filter(en -> !Engrave.COMMON.equals(en))
                .distinct()
                .findFirst()
                .ifPresent(lastVersionVideo::setEngrave);

        videoSaveManager.nofityCompleted(video);

        var reservedVideoList = new ReservedVideoList();

        reservedVideoList.addVideo(video);

        calculationReservedVideoRepository.saveAll(reservedVideoList.getReservedVideoList());
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

        return videoFileUtils.getOcrImageResult(cardNameVideoList, cardImageVideoList);
    }

    private List<CardLifePeriod> getCardLifePeriodList(List<Card> ocrCardList) {
        if(ocrCardList == null || ocrCardList.isEmpty()) return Collections.emptyList();

        var cardLifePeriodList = new ArrayList<CardLifePeriod>();

        var currentCard = ocrCardList.getFirst();
        var start = 0;

        for(var i = 1; i < ocrCardList.size(); i++) {
            if(!ocrCardList.get(i).equals(currentCard)) {
                var cardLifePeriod = new CardLifePeriod(currentCard, start, i - 1);
                start = i;
                currentCard = ocrCardList.get(i);
                cardLifePeriodList.add(cardLifePeriod);
            }
        }

        cardLifePeriodList.add(new CardLifePeriod(currentCard, start, ocrCardList.size() - 1));

        return cardLifePeriodList;
    }

    private List<CardLifePeriod> getCardLifePeriodListIndependentTrials(List<CardLifePeriod> zCardList, List<CardLifePeriod> xCardList) {
        if(zCardList == null || zCardList.isEmpty()) return Collections.emptyList();
        if(xCardList == null || xCardList.isEmpty()) return Collections.emptyList();

        var cardLifePeriodList = new ArrayList<CardLifePeriod>();

        var xCardIterator = xCardList.iterator();

        var xCard = xCardIterator.next();

        for(CardLifePeriod zCard : zCardList) {
            if(Card.EMPTY.equals(zCard.card())) continue;
            xCard = addIndependentTrials(cardLifePeriodList, zCard, xCard, xCardIterator);
        }
        return cardLifePeriodList;
    }

    private CardLifePeriod addIndependentTrials(List<CardLifePeriod> independentTrialsList, CardLifePeriod zCard, CardLifePeriod xCard, Iterator<CardLifePeriod> xCardIterator) {
        if(zCard.start() > xCard.end()){
            if(xCardIterator.hasNext()){
                xCard = xCardIterator.next();
                xCard = addIndependentTrials(independentTrialsList, zCard, xCard, xCardIterator);
            }
        }else {
            if(Card.EMPTY.equals(xCard.card()) && zCard.start() >= xCard.start()) independentTrialsList.add(zCard);
        }
        return xCard;
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

    private Map<String, Card> getValidCardMap() {
        var validCardMap = new HashMap<String, Card>();
        validCardMap.putAll(getRecalculatedCardMap());
        validCardMap.putAll(getOcrCardMap());
        return validCardMap;
    }

    private Map<String, Card> getRecalculatedCardMap() {
        var recalculatedCardMap = new HashMap<String, Card>();
        recalculatedCardMap.put("black", Card.EMPTY);
        recalculatedCardMap.put("busik", Card.부식);
        recalculatedCardMap.put("byul", Card.별);
        recalculatedCardMap.put("changing", Card.EMPTY);
        recalculatedCardMap.put("dal", Card.달);
        recalculatedCardMap.put("diun", Card.뒤운);
        recalculatedCardMap.put("dotae", Card.도태);
        recalculatedCardMap.put("gwangdae", Card.광대);
        recalculatedCardMap.put("gwangki", Card.광기);
        recalculatedCardMap.put("gyunhyun", Card.균형);
        recalculatedCardMap.put("hwanhui", Card.환희);
        recalculatedCardMap.put("royal", Card.로열);
        recalculatedCardMap.put("samdusa", Card.삼두사);
        recalculatedCardMap.put("simpan", Card.심판);
        recalculatedCardMap.put("undefined", Card.EMPTY);
        recalculatedCardMap.put("unsu", Card.운수);
        recalculatedCardMap.put("yuryeong", Card.유령);
        recalculatedCardMap.put("zEmpty", Card.EMPTY);
        recalculatedCardMap.put("xEmpty", Card.EMPTY);
        return recalculatedCardMap;
    }

    private Map<String, Card> getOcrCardMap() {
        var cardNameList = new HashMap<Card, List<String>>();

        cardNameList.put(Card.운수, List.of("운명의수레바퀴", "문짱역수래비휘", "문령역수래바퀴", "운명의수레바퀴", "운명의수래바퀴", "0쟁머수래비퀴", "그수재비낌", "운령의수레비퀴", "온령의수레바퀴", "온명이수레바퀴", "운명역수래바퀴", "문명의수래바퀴", "문명여수레바퀴", "운명이수레바퀴", "^수레비쿼", "둔랭의수레바쿼", "운령더수래빠뒤", "운평이수래바랑", "운평릭수레바퀴", "문명의수래바퀴", "운명억수래바퀴", "운명억수래바취"));
        cardNameList.put(Card.EMPTY, List.of("ㅅ","^", "ㅅ×","×", "ㅅ%", "ㅅ《", "시종 네기", "때", "|", "1", "<", "1.", "^×", "| '", "", "2", "\\\"4", "1?", "나 /앨", "?"));
        cardNameList.put(Card.부식, List.of("부식", "루서", "투석", "누이", "시"));
        cardNameList.put(Card.달, List.of("달", "들", "알", "당", "할", "항", "를", "답", "'들"));
        cardNameList.put(Card.로열, List.of("로열"));
        cardNameList.put(Card.심판, List.of("심판", "섬만", "섬딴", "섬반", "심딴", "심판", "백란", "심본", "심떤", "섬반", "설떤", "설편", "설판", "심만", "!심란", "심반", "설레", "심먼", "심딴", "심턴", "샴편", "삼만", "심편"));
        cardNameList.put(Card.광기, List.of("광기", "깅기", "왕기", "윙기", "황기", "랑기", "굴기", "1 1광기"));
        cardNameList.put(Card.도태, List.of("도태", "드태", "도대", "도때", "드대"));
        cardNameList.put(Card.별, List.of("덩"));
        cardNameList.put(Card.균형, List.of("균형", "관영", "군영", "균영", "관형"));
        cardNameList.put(Card.뒤운, List.of("뒤틀린 운명", "뒤출린 은면", "뒤출린 은", "뒤총린 운영", "뒤출린 운영", "뒤올린 운명", "뒤돌린 운명", "뒤들린 운명"));
        cardNameList.put(Card.유령, List.of("유령", "유행", "유얼", "유혈", "유정", "유햄", "유랭", "유림", "유영", "유랭", "{유형", "유형"));
        cardNameList.put(Card.삼두사, List.of("삼두사", "두사"));

        var ocrCardMap = new HashMap<String, Card>();

        for(Map.Entry<Card, List<String>> entry : cardNameList.entrySet()) {
            var card = entry.getKey();
            entry.getValue().forEach(name -> ocrCardMap.put(name, card));
        }

        return ocrCardMap;
    }

    private List<Card> toCardEnumList(List<String> cardList){
        var validCardMap = getValidCardMap();
        return cardList.stream().map(validCardMap::get).map(card -> card != null ? card : Card.EMPTY).collect(Collectors.toList());
    }
}