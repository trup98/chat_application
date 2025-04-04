package com.learning.real_time_chat_application.projection;

public interface GetAllUser {

    Long getId();

    String getUserName();

    String getEmail();

    String getUserProfileS3Url();
}
