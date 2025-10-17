package com.example.blog_website.controller;

import com.example.blog_website.dto.UserRegistrationDto;
import com.example.blog_website.dto.UserResponseDto;
import com.example.blog_website.model.User;
import com.example.blog_website.repository.UserRepository;
import com.example.blog_website.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository){
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegistrationDto dto){
        UserResponseDto response = userService.registerUser(dto);


        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/users/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication){
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<String> deleteOwnAccount(Authentication authentication){
        String email = authentication.getName();
        userService.deleteOwnAccount(email);
        return ResponseEntity.ok("Your account has been deleted successfully.");
    }
}
