package com.example.blog_website.service;

import com.example.blog_website.dto.PostRequestDto;
import com.example.blog_website.dto.PostResponseDto;
import com.example.blog_website.model.Post;

import java.util.List;

public interface PostService {
    PostResponseDto createPost(PostRequestDto dto, String currentUsername);
    PostResponseDto updatePost(Long postId, PostRequestDto dto, String currentUsername);
    void deletePost(Long postId, String currentUsername);
    PostResponseDto getPostById(Long postId);
    List<PostResponseDto> getAllPosts();
    PostResponseDto likePost(Long postId, String currentUsername);
    PostResponseDto unlikePost(Long postId, String currentUsername);
    List<Post> getPostsByAuthorEmail(String email);
}
