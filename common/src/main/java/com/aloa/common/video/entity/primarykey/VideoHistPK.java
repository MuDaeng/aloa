package com.aloa.common.video.entity.primarykey;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoHistPK implements Serializable {
    private Long id;

    private Integer histSequence;
}
