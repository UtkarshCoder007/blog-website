package com.example.blog_website.dto;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String username;
    private final String email;
    private final Set<String> roles;
    private final LocalDateTime createdAt;

    public UserResponseDto(Long id, String username, String email, Set<String> roles, LocalDateTime createdAt){
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.createdAt = createdAt;
    }

}
