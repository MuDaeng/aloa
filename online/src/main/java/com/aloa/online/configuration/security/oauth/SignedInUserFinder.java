package com.aloa.online.configuration.security.oauth;

import com.aloa.common.user.entitiy.User;
import com.aloa.common.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SignedInUserFinder {
    private final UserRepository userRepository;

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

    public String findSignedInUserId(){
        var userId = SignedInUserFinder.getSignedInUser().getUserId();

        return userRepository.findById(userId)
                .map(User::getGoogleUserId)
                .orElseThrow(() -> new IllegalArgumentException("UnCorrect Signed In User"));
    }
}
