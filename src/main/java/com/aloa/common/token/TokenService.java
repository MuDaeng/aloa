package com.aloa.common.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenRepository tokenRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reissueToken(Token token) {
        tokenRepository.save(token);
    }

    public Optional<Token> findByAccessToken(String accessToken) {
        return tokenRepository.findByAccessToken(accessToken);
    }

    public void deleteByAccessToken(String accessToken) {
        findByAccessToken(accessToken).ifPresent(tokenRepository::delete);
    }
}
