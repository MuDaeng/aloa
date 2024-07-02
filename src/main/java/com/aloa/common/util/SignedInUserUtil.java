package com.aloa.common.util;

import com.aloa.configuration.oauth.OAuth2UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SignedInUserUtil {
    public static SignedInUser getSignedInUser() {

        SignedInUser empty = SignedInUser.builder().loginUser(Optional.empty()).build();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null){
            return empty;
        }

        Object signedInUserInfo = authentication.getPrincipal();

        if(signedInUserInfo instanceof OAuth2UserInfo auth2UserInfo) {
            return SignedInUser.builder().loginUser(Optional.of(auth2UserInfo)).build();
        }

        return empty;
    }
}
