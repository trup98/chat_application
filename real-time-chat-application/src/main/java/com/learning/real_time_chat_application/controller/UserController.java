package com.learning.real_time_chat_application.controller;

import com.learning.real_time_chat_application.dto.request.RegisterUserRequestDto;
import com.learning.real_time_chat_application.dto.request.UpdateUserRequestDto;
import com.learning.real_time_chat_application.dto.response.ApiResponse;
import com.learning.real_time_chat_application.dto.response.GetAllUserDto;
import com.learning.real_time_chat_application.dto.response.UserProfileResponseDTO;
import com.learning.real_time_chat_application.enums.GetSortBy;
import com.learning.real_time_chat_application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class UserController {
    private final UserService userService;

    @PostMapping("/addUser")
    public ResponseEntity<ApiResponse> addNewUser(@RequestBody RegisterUserRequestDto registerUserRequestDto) {
        this.userService.addNewUser(registerUserRequestDto);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "User Register Successfully", Collections.emptyMap()), HttpStatus.OK);
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<ApiResponse> getAllUser(@RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                                  @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                                  @RequestParam(value = "sortBy", required = false, defaultValue = "ID") GetSortBy getSortBy,
                                                  @RequestParam(value = "sortOrder", required = false, defaultValue = "ASC") Sort.Direction sortOrder,
                                                  @RequestParam(value = "searchKey", required = false, defaultValue = "") String searchKey,
                                                  @RequestParam(value = "senderId", required = false) Long loggedInUserId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortOrder, getSortBy.getValue()));
        Page<GetAllUserDto> userResponse = this.userService.findAllUser(pageable, searchKey, loggedInUserId);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "User found Successfully", userResponse), HttpStatus.OK);
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id) {
        var userResponse = this.userService.getUserById(id);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "User found Successfully", userResponse), HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        this.userService.deleteUser(id);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "User deleted successfully", Collections.emptyMap()), HttpStatus.OK);
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        this.userService.updateUser(id, updateUserRequestDto);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "User updated successfully", Collections.emptyMap()), HttpStatus.OK);
    }

    @PostMapping("/setProfile/{id}")
    public ResponseEntity<ApiResponse> setProfile(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        UserProfileResponseDTO userProfileResponseDTO = this.userService.setUserProfile(file, id);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Profile Uploaded Successfully", userProfileResponseDTO), HttpStatus.OK);
    }

    @DeleteMapping("/remove/profile/{id}")
    public ResponseEntity<ApiResponse> removeProfile(@PathVariable Long id) {
        this.userService.removeProfilePicture(id);
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Profile Removed Successfully", Collections.emptyMap()), HttpStatus.OK);
    }


}
