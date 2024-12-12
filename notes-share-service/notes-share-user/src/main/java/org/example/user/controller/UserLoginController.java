package org.example.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.common.dtos.ResponseResult;
import org.example.model.user.dtos.LoginDto;
import org.example.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class UserLoginController {
    private final UserService userService;
    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto dto) {
        return userService.login(dto);
    }
}
