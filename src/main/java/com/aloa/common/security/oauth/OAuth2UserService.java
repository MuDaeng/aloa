package com.aloa.common.security.oauth;

import com.aloa.common.security.PrincipalDetails;
import com.aloa.common.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

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

        // 5. 회원가입 및 로그인
        getOrSave(oAuth2UserInfo);

        // 6. OAuth2User로 반환
        return new PrincipalDetails(oAuth2UserInfo, oAuth2UserAttributes, userNameAttributeName);
    }

    private void getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        var user = userRepository.findByGoogleUserId(oAuth2UserInfo.email())
                .orElseGet(oAuth2UserInfo::toUser);
        userRepository.save(user);
    }
}
