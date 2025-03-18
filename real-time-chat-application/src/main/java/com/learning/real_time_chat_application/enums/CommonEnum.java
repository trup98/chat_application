package com.learning.real_time_chat_application.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonEnum {

    AUTHORIZATION("Authorization"),
    USER_ID("userId"),
    USER_ROLE("userRole"),
    USER_NAME("userName"),
    YOUR_OTP_CODE_IS("Your OTP code is:");
    private final String value;

}
