package com.example.capstone.feed;

import com.example.capstone.auth.User;
import com.example.capstone.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Page<Post> list(String keyword, Pageable pageable) {
        String k = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return postRepository.search(k, pageable);
    }

    @Transactional
    public Post create(String creatorUsername, @Valid PostRequest req) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new EntityNotFoundException("Creator not found: " + creatorUsername));
        Post p = new Post();
        p.setTitle(req.getTitle());
        p.setContent(req.getContent());
        p.setImageUrl(req.getImageUrl());
        p.setCreatedBy(creator);
        return postRepository.save(p);
    }

    @Transactional
    public Post update(Long postId, String requesterUsername, @Valid PostRequest req) {
        Post p = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));
        if (!p.getCreatedBy().getUsername().equals(requesterUsername)) {
            throw new SecurityException("Only the author can update this post");
        }
        p.setTitle(req.getTitle());
        p.setContent(req.getContent());
        p.setImageUrl(req.getImageUrl());
        return postRepository.save(p);
    }

    public Post get(Long id) {
        return postRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + id));
    }
}
