package com.example.capstone.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notifications;

    @EventListener
    public void onPetRequest(PetRequestCreatedEvent ev) {
        // Do not notify the actor about their own action
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
        // Do not notify the sender about their own message
        if (ev.getRecipientUserId() == null || ev.getRecipientUserId().equals(ev.getSenderUserId())) return;

        notifications.create(Notification.builder()
                .recipientId(ev.getRecipientUserId())
                .type(Notification.Type.CHAT_MESSAGE)
                .title("New message")
                .message(ev.getPreview())
                .link(ev.getLink() != null ? ev.getLink() : "/chat/" + ev.getChatId())
                .build());
    }
}
