package org.ajeet.auth.dto;

public record AuthResponse(String token, String name, String email) {
}
