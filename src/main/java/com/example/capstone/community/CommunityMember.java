package com.example.capstone.community;

import com.example.capstone.auth.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "community_members",
        uniqueConstraints = @UniqueConstraint(name = "uk_member", columnNames = {"community_id", "user_id"}),
        indexes = @Index(name = "idx_member_comm", columnList = "community_id"))
public class CommunityMember {
    public enum Role {OWNER, MODERATOR, MEMBER}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Community community;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private Role role = Role.MEMBER;

    private LocalDateTime joinedAt;

    @PrePersist
    void prePersist() {
        joinedAt = LocalDateTime.now();
    }

    // getters/setters
}
