package com.aloa.common.user.entitiy;

import lombok.Data;

import java.io.Serializable;

@Data
public class LostArkCharacterPK implements Serializable {
    private Long expeditionId;

    private int sequence;
}
