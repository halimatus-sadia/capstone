// src/main/java/com/example/capstone/notification/CommunityPostCreatedEvent.java
package com.example.capstone.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityPostCreatedEvent {
    private final Long actorUserId;   // who created the post
    private final Long communityId;
    private final Long postId;
    private final String titlePreview; // optional short title/message preview
    private final String link;        // e.g. "/communities/{id}#post-{postId}"
}
