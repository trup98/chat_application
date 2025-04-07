package com.learning.real_time_chat_application.service;

import com.learning.real_time_chat_application.dto.request.GroupCreationRequestDto;
import com.learning.real_time_chat_application.dto.request.GroupMessageRequestDto;
import com.learning.real_time_chat_application.dto.response.GroupMemberDTO;
import com.learning.real_time_chat_application.dto.response.GroupMessageResponse;
import com.learning.real_time_chat_application.dto.response.GroupResponse;
import com.learning.real_time_chat_application.dto.response.UserAvailableDTO;
import com.learning.real_time_chat_application.projection.dto.GroupDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GroupMessageService {
    GroupResponse createGroup(GroupCreationRequestDto groupCreationRequestDto);

    GroupMessageResponse sendMessageToGroup(GroupMessageRequestDto groupMessageRequestDto);

    List<GroupMessageResponse> getGroupMessages(Long groupId);

    List<GroupDTO> getAllGroups();

    List<GroupDTO> getGroupsWithAssociateUser(Long userId);

    void deleteGroup(Long groupId,Long userId);

    List<GroupMemberDTO> getMembers(Long groupId);

    void addUserToExistingGroup(Long groupId, List<Long> userIds);

    List<UserAvailableDTO> getAvailableUsers(Long groupId);

    void deleteUserFromGroup(Long groupId, Long userId);

    void setProfilePicture(Long groupId, MultipartFile file) throws IOException;

    void removeProfilePicture(Long groupId);
}
