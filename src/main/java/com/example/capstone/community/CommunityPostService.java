package com.example.capstone.community;


import com.example.capstone.auth.User;
import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class CommunityPostService {
    private final CommunityPostRepository postRepo;
    private final CommunityRepository communityRepo;
    private final CommunityMemberRepository memberRepo;
    private final AuthUtils auth;

    public Page<CommunityPost> pageByCommunity(Long communityId, Pageable pageable) {
        return postRepo.findByCommunityId(communityId, pageable);
    }

    @Transactional
    public CommunityPost create(Long communityId, PostRequestDto dto) {
        User user = auth.getLoggedInUser();
        ensureMember(communityId, user.getId());

        CommunityPost p = new CommunityPost();
        p.setCommunity(communityRepo.getReferenceById(communityId));
        p.setAuthor(user);
        p.setTitle(dto.getTitle());
        p.setContent(dto.getContent());
        p.setImageUrl(dto.getImageUrl());
        return postRepo.save(p);
    }

    @Transactional
    public CommunityPost update(Long postId, PostRequestDto dto) {
        User user = auth.getLoggedInUser();
        CommunityPost p = postRepo.findById(postId).orElseThrow();
        if (!p.getAuthor().getId().equals(user.getId())) {
            // allow mods/owners too
            var mm = memberRepo.findByCommunityIdAndUserId(p.getCommunity().getId(), user.getId()).orElseThrow();
            if (mm.getRole() == CommunityMember.Role.MEMBER) throw new IllegalStateException("Not allowed.");
        }
        p.setTitle(dto.getTitle());
        p.setContent(dto.getContent());
        p.setImageUrl(dto.getImageUrl());
        return p;
    }

    private void ensureMember(Long communityId, Long userId) {
        if (!memberRepo.existsByCommunityIdAndUserId(communityId, userId))
            throw new IllegalStateException("Join the community to post.");
    }
}
