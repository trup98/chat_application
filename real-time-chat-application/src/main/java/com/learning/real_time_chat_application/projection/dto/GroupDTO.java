package com.learning.real_time_chat_application.projection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupDTO {
    private Long id;
    private String name;
    private Long memberCount;
    private String groupImage;
}
