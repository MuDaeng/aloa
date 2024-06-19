package com.aloa.common.user.entitiy.primarykey;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class LostArkCharacterPK implements Serializable {
    @NotNull
    private Long expeditionId;

    @NotNull
    private int sequence;
}
