package com.fitnessai.platform.user.vo;

import java.util.List;

public record UserVO(Long id, String username, String nickname, String email, List<String> roles) {
}
