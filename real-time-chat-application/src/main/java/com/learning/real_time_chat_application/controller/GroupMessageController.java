package com.learning.real_time_chat_application.controller;

import com.learning.real_time_chat_application.dto.request.GroupCreationRequestDto;
import com.learning.real_time_chat_application.dto.request.GroupMessageRequestDto;
import com.learning.real_time_chat_application.dto.response.ApiResponse;
import com.learning.real_time_chat_application.dto.response.GroupMessageResponse;
import com.learning.real_time_chat_application.dto.response.GroupResponse;
import com.learning.real_time_chat_application.projection.dto.GroupDTO;
import com.learning.real_time_chat_application.service.GroupMessageService;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.ast.tree.expression.Collation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/group/messages")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class GroupMessageController {

    private final GroupMessageService groupMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/crete")
    public ResponseEntity<ApiResponse> createGroup(@RequestBody GroupCreationRequestDto groupCreationRequestDto) {
        GroupResponse createdGroupResponse = this.groupMessageService.createGroup(groupCreationRequestDto);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Group Created Successfully", createdGroupResponse), HttpStatus.OK);
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendMessageToGroup(@RequestBody GroupMessageRequestDto groupMessageRequestDto) {
        GroupMessageResponse groupMessageResponse = this.groupMessageService.sendMessageToGroup(groupMessageRequestDto);
        messagingTemplate.convertAndSend("/topic/group/" + groupMessageRequestDto.getGroupId(), groupMessageResponse);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Message Sent Successfully In Group", groupMessageResponse), HttpStatus.OK);
    }

    @GetMapping("/getGroupMessage/{groupId}")
    public ResponseEntity<ApiResponse> getGroup(@PathVariable Long groupId) {
        List<GroupMessageResponse> groupMessageResponses = this.groupMessageService.getGroupMessages(groupId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Messages Found Successfully In Group", groupMessageResponses), HttpStatus.OK);
    }

    @GetMapping("/getGroups")
    public ResponseEntity<ApiResponse> getGroups() {
        List<GroupDTO> allGroups = this.groupMessageService.getAllGroups();
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Group Found Successfully", allGroups), HttpStatus.OK);
    }

    @GetMapping("/getGroup/user/{userId}")
    public ResponseEntity<ApiResponse> getGroupsWithAssociateUser(@PathVariable Long userId) {
        List<GroupDTO> allGroupAssociateUser = this.groupMessageService.getGroupsWithAssociateUser(userId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Group Found Successfully", allGroupAssociateUser), HttpStatus.OK);
    }

    @DeleteMapping("/deleteGroup/{groupId}/{senderId}")
    public ResponseEntity<ApiResponse> deleteGroup(@PathVariable Long groupId, @PathVariable Long senderId) {
        this.groupMessageService.deleteGroup(groupId, senderId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Group Found Successfully", Collections.emptyMap()), HttpStatus.OK);
    }

}
