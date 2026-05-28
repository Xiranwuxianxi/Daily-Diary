package com.diary.dto;

public class LoginResponse {
    private String token;
    private Long userId;
    private String nickname;

    public LoginResponse() {}

    public LoginResponse(String token, Long userId, String nickname) {
        this.token = token;
        this.userId = userId;
        this.nickname = nickname;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}