package com.aloa.common.user.handler;

import com.aloa.common.user.entitiy.User;
import com.aloa.common.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserRepository userRepository;

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }
}
