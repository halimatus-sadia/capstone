// src/main/java/com/example/capstone/notification/DomainEventPublisher.java
package com.example.capstone.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void petRequestCreated(Long senderUserId, Long recipientUserId,
                                  Long petRequestId, String summary, String link) {
        publisher.publishEvent(new PetRequestCreatedEvent(
                senderUserId, recipientUserId, petRequestId, summary, link));
    }

    public void chatMessageReceived(Long senderUserId, Long recipientUserId,
                                    Long chatId, String preview, String link) {
        publisher.publishEvent(new ChatMessageReceivedEvent(
                senderUserId, recipientUserId, chatId, preview, link));
    }

    // --- Community domain events ---

    public void communityPostCreated(Long actorUserId, Long communityId, Long postId,
                                     String titlePreview, String link) {
        publisher.publishEvent(new CommunityPostCreatedEvent(
                actorUserId, communityId, postId, titlePreview, link));
    }

    public void communityPostUpdated(Long actorUserId, Long communityId, Long postId,
                                     String titlePreview, String link) {
        publisher.publishEvent(new CommunityPostUpdatedEvent(
                actorUserId, communityId, postId, titlePreview, link));
    }

    public void communityCommentAdded(Long actorUserId, Long communityId, Long postId,
                                      Long parentAuthorIdOrNull, String messagePreview, String link) {
        publisher.publishEvent(new CommunityCommentAddedEvent(
                actorUserId, communityId, postId, parentAuthorIdOrNull, messagePreview, link));
    }

    public void communityJoinEvent(Long actorUserId, Long communityId, boolean joined, String link) {
        publisher.publishEvent(new CommunityJoinEvent(
                actorUserId, communityId, joined, link));
    }
}
