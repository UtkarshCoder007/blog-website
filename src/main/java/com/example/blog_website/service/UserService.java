package com.example.blog_website.service;

import com.example.blog_website.dto.UserRegistrationDto;
import com.example.blog_website.dto.UserResponseDto;
import com.example.blog_website.model.User;
import java.util.Optional;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationDto dto);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
