package org.ajeet.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 2, max = 100) String name,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{6,14}$",
                message = "phone must be in E.164 format, e.g. +919876543210") String phone,
        @NotBlank @Size(min = 8, max = 72) String password) {
}
