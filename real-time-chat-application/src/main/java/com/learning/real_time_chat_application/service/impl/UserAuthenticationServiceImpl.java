package com.learning.real_time_chat_application.service.impl;

import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.entity.UserRoleMappingEntity;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import com.learning.real_time_chat_application.repository.UserRepository;
import com.learning.real_time_chat_application.repository.UserRoleMappingRepository;
import com.learning.real_time_chat_application.service.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserRepository userRepository;
    private final UserRoleMappingRepository userRoleMappingRepository;

    @Override
    public Optional<UserDetails> findUserByEmail(String userName) {

        UserEntity userEntity = this.userRepository.findByEmail(userName).orElseThrow(() -> new CustomException(ExceptionEnum.USER_ROLE_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        List<GrantedAuthority> authorityList = new ArrayList<>();

        UserRoleMappingEntity userRoleMappingEntity = this.userRoleMappingRepository.findByUserId(userEntity).orElseThrow(() -> new RuntimeException(ExceptionEnum.USER_NOT_FOUND.getValue()));

        authorityList.add(new SimpleGrantedAuthority(userRoleMappingEntity.getRoleId().getRole()));

        return Optional.of(new User(userEntity.getEmail(), userEntity.getPassword(), authorityList));
    }
}
