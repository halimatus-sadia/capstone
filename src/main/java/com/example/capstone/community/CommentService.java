// src/main/java/com/example/capstone/community/CommentService.java
package com.example.capstone.community;

import com.example.capstone.notification.DomainEventPublisher;
import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostCommentRepository commentRepo;
    private final CommunityPostRepository postRepo;
    private final AuthUtils auth;
    private final DomainEventPublisher events;

    @Transactional
    public PostComment add(Long postId, CommentRequestDto dto) {
        var user = auth.getLoggedInUser();
        CommunityPost post = postRepo.findById(postId).orElseThrow();

        PostComment c = new PostComment();
        c.setPost(post);
        c.setAuthor(user);
        c.setContent(dto.getContent());

        Long parentAuthorId = null;
        if (dto.getParentId() != null) {
            PostComment parent = commentRepo.findById(dto.getParentId()).orElseThrow();
            if (!parent.getPost().getId().equals(postId))
                throw new IllegalArgumentException("Parent mismatch.");
            c.setParent(parent);
            parentAuthorId = parent.getAuthor().getId();
        }
        PostComment saved = commentRepo.save(c);

        String link = "/communities/" + post.getCommunity().getId() + "#post-" + post.getId();
        String preview = dto.getContent();
        events.communityCommentAdded(
                user.getId(),
                post.getCommunity().getId(),
                post.getId(),
                parentAuthorId,
                preview,
                link
        );

        return saved;
    }
}
