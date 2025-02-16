package com.aloa.online.configuration.security.oauth;

import com.aloa.online.configuration.security.PrincipalDetails;
import com.aloa.common.user.entitiy.GoogleMapping;
import com.aloa.common.user.entitiy.User;
import com.aloa.common.user.repository.GoogleMappingRepository;
import com.aloa.common.user.repository.UserRepository;
import com.aloa.common.video.handler.GoogleApiManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final GoogleMappingRepository googleMappingRepository;
    private final GoogleApiManager googleApiManager;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 3. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.builder()
                .name((String) oAuth2UserAttributes.get("name"))
                .email((String) oAuth2UserAttributes.get("email"))
                .build();

        var oAuthAccessToken = userRequest.getAccessToken();

        // 5. 회원가입 및 로그인
        var user = getOrSave(oAuth2UserInfo, oAuthAccessToken);

        // 6. 개인정보 보호로 key값을 email -> id로 변경
        oAuth2UserInfo = OAuth2UserInfo.builder()
                .name(oAuth2UserInfo.name())
                .email(String.valueOf(user.getId()))
                .build();

        // 7. OAuth2User로 반환
        return new PrincipalDetails(oAuth2UserInfo, oAuth2UserAttributes, userNameAttributeName);
    }

    private User getOrSave(OAuth2UserInfo oAuth2UserInfo, OAuth2AccessToken oAuth2AccessToken) {
        var user = userRepository.findByGoogleUserId(oAuth2UserInfo.email())
                .orElseGet(oAuth2UserInfo::toUser);
        userRepository.save(user);

        var channelId = googleApiManager.getChannelId(oAuth2AccessToken.getTokenValue());

        var googleMapping = GoogleMapping.mapChannel(oAuth2UserInfo.email(), channelId);

        googleMappingRepository.save(googleMapping);

        return user;
    }
}
