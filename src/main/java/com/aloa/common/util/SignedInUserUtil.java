package com.aloa.common.util;

import com.aloa.common.security.PrincipalDetails;
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

        if(principal instanceof PrincipalDetails principalDetails) {
            return SignedInUser.builder().user(Optional.ofNullable(principalDetails.getOAuth2UserInfo())).build();
        }

        return empty;
    }
}
