package com.example.blog_website.service.impl;

import com.example.blog_website.dto.PostResponseDto;
import com.example.blog_website.dto.PostRequestDto;
import com.example.blog_website.model.User;
import com.example.blog_website.model.Post;
import com.example.blog_website.repository.PostRepository;
import com.example.blog_website.repository.UserRepository;
import com.example.blog_website.service.PostService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PostResponseDto createPost(PostRequestDto dto, String currentUsername){
        User author = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .createdAt(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);
        return mapToDto(saved);
    }

    @Override
    public PostResponseDto updatePost(Long postId, PostRequestDto dto, String currentUsername){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Post not found!") );

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found!"));

        boolean isAuthor = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

        if(!isAuthor && !isAdmin){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to edit this post");
        }
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        Post updated = postRepository.save(post);
        return mapToDto(updated);
    }

    @Override
    public PostResponseDto getPostById(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));
        return mapToDto(post);
    }

    @Override
    public void deletePost(Long postId, String currentUsername){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        User currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));

        boolean isAuthor = post.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

        if(!isAuthor && !isAdmin){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this post.");
        }
        postRepository.delete(post);
    }

    @Override
    public PostResponseDto likePost(Long postId, String currentUsername){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));
        User user = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));

        if(post.getLikedBy().contains(user)){
                return mapToDto(post);
        }
        post.addLike(user);
        Post updated = postRepository.save(post);
        return mapToDto(updated);
    }

    @Override
    public PostResponseDto unlikePost(Long postId, String currentUsername){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));


        if(!post.getLikedBy().contains(user)){
            return mapToDto(post);
        }
        post.removeLike(user);
        Post updated = postRepository.save(post);
        return mapToDto(updated);
    }

    @Override
    public List<Post> getPostsByAuthorEmail(String email){
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(("User not found!")));
        return postRepository.findByAuthor(author);
    }

    @Override
    public List<PostResponseDto> getAllPosts(){
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private PostResponseDto mapToDto(Post p){
        PostResponseDto dto = new PostResponseDto();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setContent(p.getContent());
        dto.setAuthorUsername(p.getAuthor().getUsername());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        dto.setLikeCount(p.getLikedBy() != null ? p.getLikedBy().size() : 0);
        return dto;
    }


}
