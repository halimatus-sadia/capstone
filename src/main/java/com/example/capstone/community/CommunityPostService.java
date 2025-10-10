// src/main/java/com/example/capstone/community/CommunityPostService.java
package com.example.capstone.community;

import com.example.capstone.notification.DomainEventPublisher;
import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostService {
    private final CommunityPostRepository postRepo;
    private final CommunityRepository communityRepo;
    private final CommunityMemberRepository memberRepo;
    private final AuthUtils auth;
    private final DomainEventPublisher events;

    public Page<CommunityPost> pageByCommunity(Long communityId, Pageable pageable) {
        return postRepo.findByCommunityId(communityId, pageable);
    }

    @Transactional
    public CommunityPost create(Long communityId, PostRequestDto dto) {
        var user = auth.getLoggedInUser();
        ensureMember(communityId, user.getId());

        CommunityPost p = new CommunityPost();
        p.setCommunity(communityRepo.getReferenceById(communityId));
        p.setAuthor(user);
        p.setTitle(dto.getTitle());
        p.setContent(dto.getContent());
        p.setImageUrl(dto.getImageUrl());
        CommunityPost saved = postRepo.save(p);

        String link = "/communities/" + communityId + "#post-" + saved.getId();
        String preview = dto.getTitle() != null && !dto.getTitle().isBlank() ? dto.getTitle() : "A new post was created.";
        events.communityPostCreated(user.getId(), communityId, saved.getId(), preview, link);

        return saved;
    }

    @Transactional
    public CommunityPost update(Long postId, PostRequestDto dto) {
        var user = auth.getLoggedInUser();
        CommunityPost p = postRepo.findById(postId).orElseThrow();
        if (!p.getAuthor().getId().equals(user.getId())) {
            var mm = memberRepo.findByCommunityIdAndUserId(p.getCommunity().getId(), user.getId()).orElseThrow();
            if (mm.getRole() == CommunityMember.Role.MEMBER) throw new IllegalStateException("Not allowed.");
        }
        p.setTitle(dto.getTitle());
        p.setContent(dto.getContent());
        p.setImageUrl(dto.getImageUrl());

        String link = "/communities/" + p.getCommunity().getId() + "#post-" + p.getId();
        String preview = dto.getTitle() != null && !dto.getTitle().isBlank() ? dto.getTitle() : "A post was updated.";
        events.communityPostUpdated(user.getId(), p.getCommunity().getId(), p.getId(), preview, link);

        return p;
    }

    private void ensureMember(Long communityId, Long userId) {
        if (!memberRepo.existsByCommunityIdAndUserId(communityId, userId))
            throw new IllegalStateException("Join the community to post.");
    }
}
