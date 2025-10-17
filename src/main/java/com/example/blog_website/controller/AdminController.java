package com.example.blog_website.controller;

import com.example.blog_website.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> assignRole(@PathVariable Long userId,
                                             @RequestParam String roleName){
        userService.assignRole(userId, roleName);
        return ResponseEntity.ok("Role updated successfully.");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUserByAdmin(@PathVariable Long id){
        userService.deleteUserByAdmin(id);
        return ResponseEntity.ok("User with ID "+id+" has been deleted.");
    }
}
