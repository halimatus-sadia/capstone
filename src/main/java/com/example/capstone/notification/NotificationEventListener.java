// src/main/java/com/example/capstone/notification/NotificationEventListener.java
package com.example.capstone.notification;

import com.example.capstone.community.CommunityMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notifications;
    private final CommunityMemberRepository memberRepo;

    @EventListener
    public void onPetRequest(PetRequestCreatedEvent ev) {
        if (ev.getRecipientUserId() == null || ev.getRecipientUserId().equals(ev.getSenderUserId())) return;

        notifications.create(Notification.builder()
                .recipientId(ev.getRecipientUserId())
                .type(Notification.Type.PET_REQUEST)
                .title("New Pet Request")
                .message(ev.getSummary())
                .link(ev.getLink() != null ? ev.getLink() : "/pet-requests/" + ev.getPetRequestId())
                .build());
    }

    @EventListener
    public void onChat(ChatMessageReceivedEvent ev) {
        if (ev.getRecipientUserId() == null || ev.getRecipientUserId().equals(ev.getSenderUserId())) return;

        notifications.create(Notification.builder()
                .recipientId(ev.getRecipientUserId())
                .type(Notification.Type.CHAT_MESSAGE)
                .title("New message")
                .message(ev.getPreview())
                .link(ev.getLink() != null ? ev.getLink() : "/chat/" + ev.getChatId())
                .build());
    }

    // -------------------- Community events --------------------

    @EventListener
    public void onCommunityPostCreated(CommunityPostCreatedEvent ev) {
        List<Long> memberIds = memberRepo.findUserIdsByCommunityId(ev.getCommunityId());
        for (Long uid : memberIds) {
            if (uid == null || uid.equals(ev.getActorUserId())) continue;
            notifications.create(Notification.builder()
                    .recipientId(uid)
                    .type(Notification.Type.COMMUNITY_POST)
                    .title("New post in your community")
                    .message(truncate(ev.getTitlePreview(), 480))
                    .link(nonEmptyOr(ev.getLink(), "/communities/" + ev.getCommunityId() + "#post-" + ev.getPostId()))
                    .build());
        }
    }

    @EventListener
    public void onCommunityPostUpdated(CommunityPostUpdatedEvent ev) {
        List<Long> memberIds = memberRepo.findUserIdsByCommunityId(ev.getCommunityId());
        for (Long uid : memberIds) {
            if (uid == null || uid.equals(ev.getActorUserId())) continue;
            notifications.create(Notification.builder()
                    .recipientId(uid)
                    .type(Notification.Type.COMMUNITY_POST_UPDATED)
                    .title("Post updated")
                    .message(truncate(ev.getTitlePreview(), 480))
                    .link(nonEmptyOr(ev.getLink(), "/communities/" + ev.getCommunityId() + "#post-" + ev.getPostId()))
                    .build());
        }
    }

    @EventListener
    public void onCommunityCommentAdded(CommunityCommentAddedEvent ev) {
        // Notify all members except actor
        Set<Long> recipients = new HashSet<>(memberRepo.findUserIdsByCommunityId(ev.getCommunityId()));
        recipients.remove(ev.getActorUserId());
        // Also ensure parent author gets pinged even if not a member (and not the actor)
        if (ev.getParentAuthorIdOrNull() != null && !ev.getParentAuthorIdOrNull().equals(ev.getActorUserId())) {
            recipients.add(ev.getParentAuthorIdOrNull());
        }
        for (Long uid : recipients) {
            notifications.create(Notification.builder()
                    .recipientId(uid)
                    .type(Notification.Type.COMMUNITY_COMMENT)
                    .title("New comment")
                    .message(truncate(ev.getMessagePreview(), 480))
                    .link(nonEmptyOr(ev.getLink(), "/communities/" + ev.getCommunityId() + "#post-" + ev.getPostId()))
                    .build());
        }
    }

    @EventListener
    public void onCommunityJoin(CommunityJoinEvent ev) {
        // Broadcast join/leave to members except actor (small social signal)
        List<Long> memberIds = memberRepo.findUserIdsByCommunityId(ev.getCommunityId());
        String action = ev.isJoined() ? "joined" : "left";
        for (Long uid : memberIds) {
            if (uid == null || uid.equals(ev.getActorUserId())) continue;
            notifications.create(Notification.builder()
                    .recipientId(uid)
                    .type(Notification.Type.COMMUNITY_MEMBER)
                    .title("Member " + action)
                    .message("A member has " + action + " the community.")
                    .link(nonEmptyOr(ev.getLink(), "/communities/" + ev.getCommunityId()))
                    .build());
        }
    }

    private static String nonEmptyOr(String s, String fallback) {
        return (s != null && !s.isBlank()) ? s : fallback;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
