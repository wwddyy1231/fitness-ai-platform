package com.fitnessai.platform.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_]{4,32}$") String username,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotBlank @Size(max = 40) String nickname,
        @Email @Size(max = 100) String email) {
}
