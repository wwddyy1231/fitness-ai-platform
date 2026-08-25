package com.fitnessai.platform.user.vo;

public record LoginVO(String accessToken, String tokenType, long expiresIn, UserVO user) {
}
