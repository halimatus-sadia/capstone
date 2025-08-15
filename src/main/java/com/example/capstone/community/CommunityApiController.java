package com.example.capstone.community;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities")
public class CommunityApiController {
    private final CommunityService communityService;
    private final CommunityPostService communityPostService;
    private final CommentService commentService;

    @PostMapping("/{id}/toggle-join")
    public ResponseEntity<?> toggleJoin(@PathVariable Long id) {
        boolean joined = communityService.toggleJoin(id);
        long count = communityService.memberCount(id);
        return ResponseEntity.ok(new JoinResponse(joined, count));
    }

    @PostMapping("/{id}/posts")
    public ResponseEntity<?> createPost(@PathVariable Long id, @Valid @RequestBody PostRequestDto dto) {
        CommunityPost p = communityPostService.create(id, dto);
        return ResponseEntity.ok(new IdResponse(p.getId()));
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId, @Valid @RequestBody PostRequestDto dto) {
        CommunityPost p = communityPostService.update(postId, dto);
        return ResponseEntity.ok(new IdResponse(p.getId()));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @Valid @RequestBody CommentRequestDto dto) {
        PostComment c = commentService.add(postId, dto);
        return ResponseEntity.ok(new IdResponse(c.getId()));
    }

    record JoinResponse(boolean joined, long memberCount) {
    }

    record IdResponse(Long id) {
    }
}
