package com.example.blog_website.config;

import com.example.blog_website.model.Role;
import com.example.blog_website.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    public RoleInitializer(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args){
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_AUTHOR");
        createRoleIfNotFound("ROLE_READER");
    }
    private void createRoleIfNotFound(String roleName){
        roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
    }
}
