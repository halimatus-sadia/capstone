package com.example.capstone.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Fired when a pet request is created/updated and someone else should be notified.
 */
@Getter
@AllArgsConstructor
public class PetRequestCreatedEvent {
    private final Long senderUserId;     // the actor (who created/updated)
    private final Long recipientUserId;  // who should be notified
    private final Long petRequestId;
    private final String summary;
    private final String link;
}

/**
 * Fired when a chat message is received and recipients should be notified.
 */
@Getter
@AllArgsConstructor
class ChatMessageReceivedEvent {
    private final Long senderUserId;     // who sent the message
    private final Long recipientUserId;  // who should be notified
    private final Long chatId;
    private final String preview;
    private final String link;
}
