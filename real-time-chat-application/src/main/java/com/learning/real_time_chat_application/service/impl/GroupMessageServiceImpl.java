package com.learning.real_time_chat_application.service.impl;

import com.learning.real_time_chat_application.dto.request.GroupCreationRequestDto;
import com.learning.real_time_chat_application.dto.request.GroupMessageRequestDto;
import com.learning.real_time_chat_application.dto.response.GroupMessageResponse;
import com.learning.real_time_chat_application.dto.response.GroupResponse;
import com.learning.real_time_chat_application.entity.GroupEntity;
import com.learning.real_time_chat_application.entity.GroupMemberEntity;
import com.learning.real_time_chat_application.entity.GroupMessageEntity;
import com.learning.real_time_chat_application.entity.UserEntity;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import com.learning.real_time_chat_application.projection.dto.GroupDTO;
import com.learning.real_time_chat_application.repository.GroupMemberRepository;
import com.learning.real_time_chat_application.repository.GroupMessageRepository;
import com.learning.real_time_chat_application.repository.GroupRepository;
import com.learning.real_time_chat_application.repository.UserRepository;
import com.learning.real_time_chat_application.service.GroupMessageService;
import com.learning.real_time_chat_application.utill.Utilities;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMessageServiceImpl implements GroupMessageService {
    private final GroupRepository groupRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final Utilities utilities;

    @Override
    public GroupResponse createGroup(GroupCreationRequestDto groupCreationRequestDto) {

        UserEntity currentUser = utilities.currentUser();

        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setGroupName(groupCreationRequestDto.getGroupName());
        groupEntity.setCreatedBy(currentUser);
        groupEntity.setUpdatedBy(currentUser);
        GroupEntity savedGroup = groupRepository.save(groupEntity);

        for (Long userId : groupCreationRequestDto.getUserIds()) {
            UserEntity user = this.userRepository.findById(userId).orElseThrow(() -> new CustomException(ExceptionEnum.USER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
            addUserToGroup(savedGroup, user, currentUser);
        }

        return mapToGroupResponse(groupEntity);
    }

    private GroupResponse mapToGroupResponse(GroupEntity groupEntity) {
        List<Long> members = groupMemberRepository.findByGroup(groupEntity)
                .stream().map(member -> member.getUser().getId()).collect(Collectors.toList());
        GroupResponse response = new GroupResponse();
        response.setGroupName(groupEntity.getGroupName());
        response.setCreatedBy(groupEntity.getCreatedBy().getId());
        response.setMembers(members);
        return response;
    }

    private void addUserToGroup(GroupEntity savedGroup, UserEntity user, UserEntity currentUser) {
        if (!groupMemberRepository.existsByGroupAndUser(savedGroup, user)) {
            GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
            groupMemberEntity.setGroup(savedGroup);
            groupMemberEntity.setUser(user);
            groupMemberEntity.setJoinedAt(LocalDateTime.now());
            groupMemberEntity.setCreatedBy(currentUser);
            groupMemberEntity.setUpdatedBy(currentUser);
            groupMemberRepository.save(groupMemberEntity);
        }
    }

    @Override
    public GroupMessageResponse sendMessageToGroup(GroupMessageRequestDto groupMessageRequestDto) {
        GroupEntity group = this.groupRepository.findById(groupMessageRequestDto.getGroupId()).orElseThrow(() -> new CustomException(ExceptionEnum.GROUP_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));
        UserEntity senderUser = this.userRepository.findById(groupMessageRequestDto.getSenderId()).orElseThrow(() -> new CustomException(ExceptionEnum.SENDER_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        GroupMessageEntity groupMessageEntity = GroupMessageEntity.builder()
                .group(group)
                .sender(senderUser)
                .content(groupMessageRequestDto.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        GroupMessageEntity saveGroupMessage = this.groupMessageRepository.save(groupMessageEntity);
        return mapToGroupMessageResponse(saveGroupMessage);
    }

    private GroupMessageResponse mapToGroupMessageResponse(GroupMessageEntity saveGroupMessage) {
        GroupMessageResponse response = new GroupMessageResponse();
        response.setGroupId(saveGroupMessage.getGroup().getId());
        response.setSenderId(saveGroupMessage.getSender().getId());
        response.setContent(saveGroupMessage.getContent());
        response.setTimestamp(saveGroupMessage.getTimestamp());
        response.setSenderName(saveGroupMessage.getSender().getUserName());
        return response;
    }

    @Override
    public List<GroupMessageResponse> getGroupMessages(Long groupId) {
        GroupEntity groupEntity = this.groupRepository.findById(groupId).orElseThrow(() -> new CustomException(ExceptionEnum.GROUP_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        return this.groupMessageRepository.findByGroupOrderByTimestampAsc(groupEntity).stream().map(this::mapToGroupMessageResponse).collect(Collectors.toList());


    }

    @Override
    public List<GroupDTO> getAllGroups() {
        return this.groupRepository.getAllGroupsWithMemberCount();
    }

    @Override
    public List<GroupDTO> getGroupsWithAssociateUser(Long userId) {
        List<GroupDTO> groupsByAssociateUser = this.groupRepository.findGroupsByUserId(userId);
        if (groupsByAssociateUser.isEmpty()) {
            throw new CustomException(ExceptionEnum.GROUP_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        }
        return groupsByAssociateUser;
    }

    @Override
    public void deleteGroup(Long groupId, Long userId) {
        GroupEntity groupEntity = this.groupRepository.findById(groupId).orElseThrow(() -> new CustomException(ExceptionEnum.GROUP_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND));

        if (!groupEntity.getCreatedBy().getId().equals(userId)) {
            throw new CustomException(ExceptionEnum.ONLY_ADMIN_CAN_DELETE_GROUP.getMessage(), HttpStatus.UNAUTHORIZED);
        }
        List<GroupMemberEntity> groupMemberEntities = this.groupMemberRepository.findByGroupId(groupEntity.getId());
        List<GroupMessageEntity> groupMessageEntity = this.groupMessageRepository.findByGroupId(groupEntity.getId());

        groupEntity.setIsDeleted(true);
        groupEntity.setIsActive(false);

        groupRepository.save(groupEntity);

        if (groupMemberEntities.isEmpty()) {
            throw new CustomException(ExceptionEnum.GROUP_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
        } else {
            for (GroupMemberEntity memberInGroup : groupMemberEntities) {
                memberInGroup.setIsDeleted(true);
                memberInGroup.setIsActive(false);
                groupMemberRepository.saveAll(groupMemberEntities);
            }
        }
//        if (groupMessageEntity.isEmpty()) {
//            throw new CustomException(ExceptionEnum.GROUP_NOT_FOUND.getMessage(), HttpStatus.NOT_FOUND);
//        } else {
//            for (GroupMessageEntity messagesInGroup : groupMessageEntity) {
//                messagesInGroup.setIsActive(false);
//                messagesInGroup.setIsDeleted(true);
//                groupMessageRepository.save(messagesInGroup);
//            }
//        }


    }
}
