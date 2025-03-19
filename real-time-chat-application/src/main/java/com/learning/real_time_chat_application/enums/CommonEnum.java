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
    YOUR_OTP_CODE_IS("Your OTP code is:"),
    SECRET_KEY("305c300d06092a864886f70d0101010500034b0030480241009f51b8e9c08d6fe888144e285fe61843ba37befa0d757cbdc37d16fc15c74053615981d5bdaf2dd9ade3624104b0ba6bab0c2aa0503af9d7321bc67c17d46d590203010001");
    private final String value;

}
