package com.learning.real_time_chat_application.service;

import com.learning.real_time_chat_application.dto.request.GroupCreationRequestDto;
import com.learning.real_time_chat_application.dto.request.GroupMessageRequestDto;
import com.learning.real_time_chat_application.dto.response.GroupMessageResponse;
import com.learning.real_time_chat_application.dto.response.GroupResponse;
import com.learning.real_time_chat_application.projection.dto.GroupDTO;

import java.util.List;

public interface GroupMessageService {
    GroupResponse createGroup(GroupCreationRequestDto groupCreationRequestDto);

    GroupMessageResponse sendMessageToGroup(GroupMessageRequestDto groupMessageRequestDto);

    List<GroupMessageResponse> getGroupMessages(Long groupId);

    List<GroupDTO> getAllGroups();

    List<GroupDTO> getGroupsWithAssociateUser(Long userId);
}
