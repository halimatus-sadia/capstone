// src/main/java/com/example/capstone/notification/CommunityPostUpdatedEvent.java
package com.example.capstone.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityPostUpdatedEvent {
    private final Long actorUserId;   // who updated the post
    private final Long communityId;
    private final Long postId;
    private final String titlePreview;
    private final String link;
}
