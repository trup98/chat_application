package com.learning.real_time_chat_application.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserAuthenticationService {
    Optional<UserDetails> findUserByEmail(String userName);
}
