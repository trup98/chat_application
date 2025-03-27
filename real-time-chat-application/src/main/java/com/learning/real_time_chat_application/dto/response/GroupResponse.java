package com.learning.real_time_chat_application.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupResponse {
    private String groupName;
    private Long createdBy;
    private LocalDateTime createdAt;
    private List<Long> members;
}
