// src/main/java/com/example/capstone/notification/Notification.java
package com.example.capstone.notification;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    public enum Type {
        PET_REQUEST,
        CHAT_MESSAGE,
        SYSTEM,
        COMMUNITY_POST,
        COMMUNITY_POST_UPDATED,
        COMMUNITY_COMMENT,
        COMMUNITY_MEMBER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // map this to your User id type
    private Long recipientId;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    // e.g. "/pet-requests/123" or "/chat/room/xyz"
    private String link;

    private boolean readFlag = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
