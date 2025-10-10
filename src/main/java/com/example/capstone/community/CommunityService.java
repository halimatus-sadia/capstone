// src/main/java/com/example/capstone/community/CommunityService.java
package com.example.capstone.community;

import com.example.capstone.notification.DomainEventPublisher;
import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepo;
    private final CommunityMemberRepository memberRepo;
    private final AuthUtils auth;
    private final DomainEventPublisher events;

    public Page<Community> browse(String q, String category, String location, Pageable pageable) {
        return communityRepo.search(emptyToNull(q), emptyToNull(category), emptyToNull(location), pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Community create(CommunityRequestDto dto) {
        var user = auth.getLoggedInUser();
        Community c = new Community();
        apply(dto, c);
        c.setCreatedBy(user);
        Community saved = communityRepo.save(c);

        CommunityMember owner = new CommunityMember();
        owner.setCommunity(saved);
        owner.setUser(user);
        owner.setRole(CommunityMember.Role.OWNER);
        memberRepo.save(owner);
        return saved;
    }

    @Transactional
    public Community update(Long id, CommunityRequestDto dto) {
        var user = auth.getLoggedInUser();
        Community c = communityRepo.findById(id).orElseThrow();
        ensureOwnerOrModerator(c.getId(), user.getId());
        apply(dto, c);
        return c;
    }

    @Transactional
    public boolean toggleJoin(Long communityId) {
        var user = auth.getLoggedInUser();
        var existing = memberRepo.findByCommunityIdAndUserId(communityId, user.getId());
        boolean joined;
        if (existing.isPresent()) {
            memberRepo.delete(existing.get());
            joined = false;
        } else {
            CommunityMember m = new CommunityMember();
            m.setCommunity(communityRepo.getReferenceById(communityId));
            m.setUser(user);
            m.setRole(CommunityMember.Role.MEMBER);
            memberRepo.save(m);
            joined = true;
        }

        String link = "/communities/" + communityId;
        events.communityJoinEvent(user.getId(), communityId, joined, link);

        return joined;
    }

    public boolean isMember(Long communityId, Long userId) {
        return memberRepo.existsByCommunityIdAndUserId(communityId, userId);
    }

    public long memberCount(Long communityId) {
        return memberRepo.countByCommunityId(communityId);
    }

    private void apply(CommunityRequestDto dto, Community c) {
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setCategory(dto.getCategory());
        c.setLocation(dto.getLocation());
        c.setCoverImageUrl(dto.getCoverImageUrl());
    }

    private void ensureOwnerOrModerator(Long communityId, Long userId) {
        var mm = memberRepo.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new IllegalStateException("Not a member."));
        if (mm.getRole() == CommunityMember.Role.MEMBER)
            throw new IllegalStateException("Insufficient permission.");
    }

    public Community findById(Long id) {
        return communityRepo.findById(id).orElseThrow(() -> new RuntimeException("Community not found!"));
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
