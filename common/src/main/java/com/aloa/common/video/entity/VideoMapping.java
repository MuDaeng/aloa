package com.aloa.common.video.entity;

import com.aloa.common.user.entitiy.LostArkCharacter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
public class VideoMapping {
    @Setter
    @Id
    private Long videoId;
    @Column(nullable = false)
    private String googleUserId;

    private Long expeditionId;

    private int sequence;

    private VideoMapping(String googleUserId){
        this.googleUserId = googleUserId;
    }

    public void mapCharacter(LostArkCharacter character){
        this.expeditionId = character.getExpeditionId();
        this.sequence = character.getSequence();
    }

    public static VideoMapping createVideoMapping(String googleUserId){
        return new VideoMapping(googleUserId);
    }
}
