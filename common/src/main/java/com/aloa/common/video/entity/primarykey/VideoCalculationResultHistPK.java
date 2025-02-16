package com.aloa.common.video.entity.primarykey;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class VideoCalculationResultHistPK extends VideoCalculationResultPK implements Serializable {
    private Integer sequence;
}
