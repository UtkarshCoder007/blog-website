package com.example.blog_website.service.impl;

import com.example.blog_website.dto.UserRegistrationDto;
import com.example.blog_website.dto.UserResponseDto;
import com.example.blog_website.model.Role;
import com.example.blog_website.repository.UserRepository;
import com.example.blog_website.repository.RoleRepository;
import com.example.blog_website.service.UserService;
import com.example.blog_website.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationDto dto){
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new RuntimeException(("Email already taken!"));
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Role defaultRole = roleRepository.findByName("ROLE_READER")
            .orElseThrow(() -> new RuntimeException("Default Role not Found!"));
        user.getRoles().add(defaultRole);

        User savedUser = userRepository.save(user);

        return new UserResponseDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()),
                savedUser.getCreatedAt()
        );
    }

    @Override
    public void deleteOwnAccount(String currentEmail){
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        userRepository.delete(user);
    }

    @Override
    public void deleteUserByAdmin(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        userRepository.delete(user);
    }

    @Override
    public void assignRole(Long userId, String roleName){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not Found!"));

        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

}
