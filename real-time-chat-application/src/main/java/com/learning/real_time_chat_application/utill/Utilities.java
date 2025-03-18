package com.learning.real_time_chat_application.utill;

import com.learning.real_time_chat_application.dto.response.TokenClaims;
import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class Utilities {

    private final UserRepository userRepository;
    private final TokenClaims tokenClaims;

    public UserEntity currentUser() {
        UserEntity userEntity = Optional.ofNullable(this.tokenClaims.getUserId())
                .flatMap(this.userRepository::findById)
                .orElse(null);
        return userEntity;
    }

}
