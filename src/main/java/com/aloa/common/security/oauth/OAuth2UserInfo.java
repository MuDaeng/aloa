package com.aloa.common.security.oauth;

import com.aloa.common.user.entitiy.User;
import com.aloa.common.user.entitiy.primarykey.UserRole;
import lombok.Builder;

@Builder
public record OAuth2UserInfo(String name, String email) {

    public User toUser(){
        return User.builder()
                .name(name)
                .googleUserId(email)
                .role(UserRole.USER)
                .build();
    }
}
