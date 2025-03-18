package com.learning.real_time_chat_application.service.impl;

import com.learning.real_time_chat_application.config.jwt.JwtTokenProvider;
import com.learning.real_time_chat_application.dto.request.LoginRequestDto;
import com.learning.real_time_chat_application.dto.response.LoginResponseDto;
import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.entity.UserRoleMappingEntity;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.enums.JwtExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import com.learning.real_time_chat_application.repository.UserRepository;
import com.learning.real_time_chat_application.repository.UserRoleMappingRepository;
import com.learning.real_time_chat_application.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleMappingRepository userRoleMappingRepository;


    @Override
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {

        this.authenticateUser(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        var user = this.getUser(loginRequestDto.getEmail());

        if (this.passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            return getTokenResponse(user);
        } else {
            throw new CustomException(ExceptionEnum.INCORRECT_EMAIL_OR_PASSWORD.getValue(), HttpStatus.UNAUTHORIZED);
        }
    }

    private UserEntity getUser(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new CustomException(JwtExceptionEnum.INCORRECT_USERNAME_OR_PASSWORD.getValue(), HttpStatus.UNAUTHORIZED));
    }

    private void authenticateUser(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (Exception e) {
            throw new CustomException(JwtExceptionEnum.INCORRECT_USERNAME_OR_PASSWORD.getValue(), HttpStatus.UNAUTHORIZED);
        }
    }

    public LoginResponseDto getTokenResponse(UserEntity userEntity) {
        String userRole;
        Optional<UserRoleMappingEntity> userRoleMappingEntity = this.userRoleMappingRepository.findByUserId(userEntity);

        if (userRoleMappingEntity.isPresent()) {
            userRole = userRoleMappingEntity.get().getRoleId().getRole();
        } else {
            throw new CustomException(ExceptionEnum.USER_ROLE_NOT_FOUND.getValue(), HttpStatus.NOT_FOUND);
        }

        try {
            return new LoginResponseDto(jwtTokenProvider.createToken(userEntity.getEmail(), userRole, userEntity.getId()), userEntity.getUserName(), userRole);
        } catch (Exception e) {
            throw new CustomException("Error While Creating Token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
