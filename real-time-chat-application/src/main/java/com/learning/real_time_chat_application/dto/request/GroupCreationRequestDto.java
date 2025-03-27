package com.learning.real_time_chat_application.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class GroupCreationRequestDto {

    private String groupName;
    private List<Long> userIds;

}
