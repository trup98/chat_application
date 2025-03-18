package com.learning.real_time_chat_application.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtExceptionEnum {
    UNAUTHORIZED("Unauthorized", "UNAUTHORIZED"),
    SOMETHING_WENT_WRONG("Something went wrong", "SOMETHING_WENT_WRONG"),
    USER_NOT_FOUND("User not found", "USER_NOT_FOUND"),
    INCORRECT_USERNAME_OR_PASSWORD("Incorrect username or password", "INCORRECT_USERNAME_OR_PASSWORD"),
    INVALID_CREDENTIALS("Invalid credentials", "INVALID_CREDENTIALS"),
    INVALID_PASSWORD("Invalid password", "INVALID_PASSWORD"),
    INVALID_TOKEN("Invalid token", "INVALID_TOKEN"),
    TOKEN_EXPIRED("Token is expired", "TOKEN_EXPIRED"),
    USER_EXISTS("User already exists", "USER_EXISTS"),
    INVALID_INPUT_PARAMS("Invalid input params", "INVALID_INPUT_PARAMS"),
    MAXIMUM_LOGIN_ATTEMPT_REACHED("Maximum login attempt reached", "MAXIMUM_LOGIN_ATTEMPT_REACHED"),
    NEW_PASSWORD_CANT_BE_AS_OLD("New password cannot be same as old password", "NEW_PASSWORD_CANT_BE_AS_OLD"),
    ACCOUNT_LOCKED("Account locked", "ACCOUNT_LOCKED"),
    USER_ALREADY_EXIST_WITH_THIS_EMAIL("User already exist with this email", "USER_ALREADY_EXIST_WITH_THIS_EMAIL"),
    MISSING_AUTHORIZATION_HEADER("Missing Authorization Header", "MISSING_AUTHORIZATION_HEADER");
    private final String value;
    private final String message;


}
