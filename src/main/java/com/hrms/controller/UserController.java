package com.hrms.controller;

import com.hrms.dto.UserDto;
import com.hrms.service.UserService;
import com.hrms.util.CustomApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<CustomApiResponse<Page<UserDto>>> getAllUsers(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<UserDto> users = userService.searchUsers(search, pageable);
        return ResponseEntity.ok(CustomApiResponse.<Page<UserDto>>builder().data(users).build());
    }
}
