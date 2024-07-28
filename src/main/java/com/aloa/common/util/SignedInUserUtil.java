package com.aloa.common.util;

import com.aloa.common.security.oauth.OAuth2UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SignedInUserUtil {
    public static SignedInUser getSignedInUser() {

        SignedInUser empty = SignedInUser.builder().user(Optional.empty()).build();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null){
            return empty;
        }

        Object principal = authentication.getPrincipal();

        if(principal instanceof OAuth2UserInfo oAuth2UserInfo) {
            return SignedInUser.builder().user(Optional.of(oAuth2UserInfo)).build();
        }


        return empty;
    }
}
