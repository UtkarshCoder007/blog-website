package com.example.blog_website.controller;

import com.example.blog_website.dto.PostResponseDto;
import com.example.blog_website.dto.PostRequestDto;
import com.example.blog_website.model.Post;
import com.example.blog_website.service.PostService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService){
        this.postService = postService;
    }


    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }


    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/my-posts")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<List<Post>> getMyPosts(Authentication authentication){
        String email = authentication.getName();
        return ResponseEntity.ok(postService.getPostsByAuthorEmail(email));
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@RequestBody PostRequestDto dto, Authentication authentication){
        String username = authentication.getName();
        PostResponseDto created = postService.createPost(dto, username);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long id,
                                                      @RequestBody PostRequestDto postRequestDto,
                                                      Authentication authentication){
        String username = authentication.getName();
        PostResponseDto updated = postService.updatePost(id, postRequestDto, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        postService.deletePost(id, username);
        return ResponseEntity.noContent().build();
    }

    
    @PostMapping("/{id}/like")
    public PostResponseDto likePost(@PathVariable Long id, Authentication authentication){
        return postService.likePost(id, authentication.getName());

    }

    @PostMapping("/{id}/unlike")
    public PostResponseDto unlikePost(@PathVariable Long id, Authentication authentication){
        return postService.unlikePost(id, authentication.getName());
    }
    
}
