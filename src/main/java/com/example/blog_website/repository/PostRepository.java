package com.example.blog_website.repository;

import com.example.blog_website.model.Post;
import com.example.blog_website.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByAuthor(User author);
}
