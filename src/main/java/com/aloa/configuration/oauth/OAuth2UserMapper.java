package com.aloa.configuration.oauth;

import com.aloa.common.user.entitiy.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OAuth2UserMapper {
    OAuth2UserMapper INSTANCE = Mappers.getMapper(OAuth2UserMapper.class);

    @Mapping(source = "googleUserId", target = "email")
    OAuth2UserInfo toOAuth2UserInfo(User user);
}
