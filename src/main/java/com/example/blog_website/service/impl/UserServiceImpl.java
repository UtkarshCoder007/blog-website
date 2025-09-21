package com.example.blog_website.service.impl;

import com.example.blog_website.dto.UserRegistrationDto;
import com.example.blog_website.dto.UserResponseDto;
import com.example.blog_website.model.Role;
import com.example.blog_website.repository.UserRepository;
import com.example.blog_website.repository.RoleRepository;
import com.example.blog_website.service.UserService;
import com.example.blog_website.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    }

}
