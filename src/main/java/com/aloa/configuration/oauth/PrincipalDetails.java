package com.aloa.configuration.oauth;

import com.aloa.common.user.entitiy.primarykey.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record PrincipalDetails(OAuth2UserInfo oAuth2UserInfo, Map<String, Object> attributes, String attributeKey) implements OAuth2User, UserDetails {


    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return oAuth2UserInfo.email();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + UserRole.USER.getCode()));
    }

    @Override
    public String getName() {
        return oAuth2UserInfo.name();
    }
}
