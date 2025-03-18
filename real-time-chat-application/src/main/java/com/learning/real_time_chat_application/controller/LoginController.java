package com.learning.real_time_chat_application.controller;


import com.learning.real_time_chat_application.dto.request.LoginRequestDto;
import com.learning.real_time_chat_application.dto.response.ApiResponse;
import com.learning.real_time_chat_application.dto.response.LoginResponseDto;
import com.learning.real_time_chat_application.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto loginResponseDto = this.loginService.loginUser(loginRequestDto);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Welcome...", loginResponseDto), HttpStatus.OK);
    }

}
