package com.diary.service;

import com.diary.dto.RegisterRequest;
import com.diary.dto.LoginRequest;
import com.diary.dto.LoginResponse;

public interface UserService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    com.diary.entity.User getUserById(Long userId);
}