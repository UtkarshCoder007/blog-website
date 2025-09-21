package com.example.blog_website.config;

import com.example.blog_website.repository.RoleRepository;
import com.example.blog_website.repository.UserRepository;
import com.example.blog_website.model.User;
import com.example.blog_website.model.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Collections;

@Component
public class AdminInitializer implements CommandLineRunner{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public AdminInitializer(UserRepository userRepository, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... run){
        if(!userRepository.existsByEmail("admin@mail.com")){
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Admin Role Not Found"));

            User admin = User.builder()
                    .username("admin")
                    .email("admin@mail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Collections.singleton(adminRole))
                    .build();
            userRepository.save(admin);
            System.out.println("Super Admin Created: admin@mail.com / admin123");
        }
    }
}
