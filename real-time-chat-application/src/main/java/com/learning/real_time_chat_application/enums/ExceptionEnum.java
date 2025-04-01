package com.learning.real_time_chat_application.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionEnum {

    OTP_EXPIRED("Otp Expired", "OTP_EXPIRED"),
    OTP_INVALID("Otp Invalid", "OTP_INVALID"),
    TOKEN_WITHOUT_ROLE("Token Not Containing role", "TOKEN_WITHOUT_ROLE"),
    UNAUTHORIZED("Unauthorized", "UNAUTHORIZED"),
    SOMETHING_WENT_WRONG("Something went wrong", "SOMETHING_WENT_WRONG"),
    USER_NAME_NOT_FOUND("UserName not found", "USER_NAME_NOT_FOUND"),
    USER_NOT_FOUND("User not found", "USER_NOT_FOUND"),
    USER_DETAILS_NOT_FOUND("User details  not found", "USER_DETAILS_NOT_FOUND"),
    USER_ROLE_MAPPING_NOT_FOUND("User Role Mapping Not Found", "USER_ROLE_MAPPING_NOT_FOUND"),
    USER_ROLE_NOT_FOUND("User role not found", "USER_ROLE_NOT_FOUND"),
    USER_GROUP_NOT_FOUND("User group not found", "USER_GROUP_NOT_FOUND"),
    INCORRECT_EMAIL_OR_PASSWORD("Incorrect Email or Password", "INCORRECT_EMAIL_OR_PASSWORD"),
    INVALID_CREDENTIALS("Invalid credentials", "INVALID_CREDENTIALS"),
    INVALID_PASSWORD("Invalid password", "INVALID_PASSWORD"),
    USER_EXISTS("User already exists", "USER_EXISTS"),
    USER_ALREADY_EXIST_WITH_THIS_EMAIL("User already exist with this email", "USER_ALREADY_EXIST_WITH_THIS_EMAIL"),
    INVALID_TOKEN("Invalid token", "INVALID_TOKEN"),
    ROLE_NOT_FOUND("Role not found", "ROLE_NOT_FOUND"),
    ROLE_ALREADY_EXIST("Role already exists", "ROLE_EXISTS"),
    ERROR_WHILE_GENERATING_SECERET_KEY("Error While generating Key", "ERROR_WHILE_GENERATING_SECERET_KEY"),
    SENDER_NOT_FOUND("Sender not found", "SENDER_NOT_FOUND"),
    RECEIVER_NOT_FOUND("Receiver not found", "RECEIVER_NOT_FOUND"),
    CONVERSATION_NOT_FOUND("Conversation not found", "CONVERSATION_NOT_FOUND"),
    GROUP_NOT_FOUND("Group not found", "GROUP_NOT_FOUND"),
    ONLY_ADMIN_CAN_DELETE_GROUP("Only Admin Can Delete Group", "ONLY_ADMIN_CAN_DELETE_GROUP"),
    GROUP_MEMBER_NOT_FOUND("Group Member not found", "GROUP_MEMBER_NOT_FOUND"),
    GROUP_OR_USER_NOT_FOUND("Group or User Not Found", "GROUP_OR_USER_NOT_FOUND"),

    ;


    private final String message;
    private final String value;
}
