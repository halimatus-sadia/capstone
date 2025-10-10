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
}
