package com.wayfarer.wayfarer_backend.controller;

import com.wayfarer.wayfarer_backend.dto.user_dto.UserResponse;
import com.wayfarer.wayfarer_backend.dto.user_dto.UserUpdateRequest;
import com.wayfarer.wayfarer_backend.service.user_service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUserProfile(request));
    }

    @DeleteMapping("/me")
    public void deleteAccount(){
        this.userService.deleteUser();
    }
}