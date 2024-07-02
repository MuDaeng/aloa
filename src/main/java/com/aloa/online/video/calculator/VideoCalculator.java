package com.aloa.online.video.calculator;

import com.aloa.common.card.entity.Card;
import com.aloa.common.video.entity.CalculationState;
import com.aloa.common.video.entity.Video;
import com.aloa.common.video.entity.VideoCalculationResult;
import com.aloa.common.video.manager.VideoSaveManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
@Transactional
public class VideoCalculator {

    private final VideoSaveManager videoSaveManager;

    public void calculate(@NonNull Video video) {
        //

        videoSaveManager.notifyDownloading(video);

        /** 구글에서 유튜브 정보 가져옴 */

        /** */

        videoSaveManager.notifyCalculating(video);
        //for문으로 계산
        //독립시행 종속시행 포함
        Map<Card, VideoCalculationResult> cardCalculationMap = initCalculation(video, false);

        Map<Card, VideoCalculationResult> cardCalculationIndependentTrials = initCalculation(video, true);

        for(;;) {
            String z = "";
            String x = "";


            Card zCard = getCardForString(z);
            Card xCard = getCardForString(x);

            boolean isIndependent = !zCard.equals(Card.EMPTY) && xCard.equals(Card.EMPTY);

            cardCalculationMap.get(zCard).incrementCnt();
            cardCalculationMap.get(xCard).incrementCnt();

            if(isIndependent){
                cardCalculationIndependentTrials.get(zCard).incrementCnt();
            }

            if(zCard.equals(Card.EMPTY)) break;
        }

        video.setCalculationState(CalculationState.COMPLETED);
        videoSaveManager.regCalculationResult(Collections.emptyList());
    }

    public Map<Card, VideoCalculationResult> initCalculation(Video video, boolean independentTrials) {
        Map<Card, VideoCalculationResult> cardCalculationMap = new HashMap<>();
        Card[] Cards = Card.values();

        for(Card card : Cards) {
            cardCalculationMap.put(card, new VideoCalculationResult(video, card, independentTrials));
        }
        return Collections.unmodifiableMap(cardCalculationMap);
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