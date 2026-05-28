package com.diary.controller;

import com.diary.common.R;
import com.diary.dto.LoginRequest;
import com.diary.dto.LoginResponse;
import com.diary.dto.RegisterRequest;
import com.diary.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public R<?> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return R.ok("注册成功");
    }

    @PostMapping("/login")
    public R<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return R.ok("登录成功", response);
    }

    @GetMapping("/me")
    public R<?> me() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return R.ok(userService.getUserById(userId));
    }
}