package com.aloa.common.user.entitiy;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class GoogleMapping {
    @Id
    private String googleUserId;

    private String channelId;

    public static GoogleMapping mapChannel(String googleUserId, String channelId){
        return new GoogleMapping(googleUserId, channelId);
    }

}
