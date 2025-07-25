package com.example.capstone.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post postDetails) {
        return postRepository.findById(id).map(post -> {
            post.setContent(postDetails.getContent());
            post.setType(postDetails.getType());
            post.setUser(postDetails.getUser());
            return postRepository.save(post);
        }).orElseGet(() -> {
            postDetails.setId(id);
            return postRepository.save(postDetails);
        });
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
} 
