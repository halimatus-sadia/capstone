package com.example.capstone.community;


import com.example.capstone.auth.User;
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

    @Transactional
    public PostComment add(Long postId, CommentRequestDto dto) {
        User user = auth.getLoggedInUser();
        CommunityPost post = postRepo.findById(postId).orElseThrow();

        PostComment c = new PostComment();
        c.setPost(post);
        c.setAuthor(user);
        c.setContent(dto.getContent());
        if (dto.getParentId() != null) {
            PostComment parent = commentRepo.findById(dto.getParentId()).orElseThrow();
            if (!parent.getPost().getId().equals(postId))
                throw new IllegalArgumentException("Parent mismatch.");
            c.setParent(parent);
        }
        return commentRepo.save(c);
    }
}
