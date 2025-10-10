// src/main/java/com/example/capstone/notification/CommunityCommentAddedEvent.java
package com.example.capstone.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityCommentAddedEvent {
    private final Long actorUserId;         // who added the comment
    private final Long communityId;
    private final Long postId;
    private final Long parentAuthorIdOrNull; // reply target, may be null
    private final String messagePreview;
    private final String link;
}
