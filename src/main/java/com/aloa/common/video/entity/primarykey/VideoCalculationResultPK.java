package com.aloa.common.video.entity.primarykey;

import com.aloa.common.card.entity.Card;
import lombok.Data;

import java.io.Serializable;

@Data
public class VideoCalculationResultPK implements Serializable {
    private Long videoId;
    private Card card;
    private boolean independentTrials;
}
