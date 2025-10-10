package com.example.capstone.pet.chat;

import com.example.capstone.auth.User;
import com.example.capstone.auth.UserRepository;
import com.example.capstone.notification.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatThreadRepository threadRepo;
    private final ChatMessageRepository msgRepo;
    private final DomainEventPublisher events;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = {Exception.class})
    public ChatThread getOrCreateThread(Long petId, Long ownerId, Long userId) {
        return threadRepo.findByPetIdAndOwnerIdAndUserId(petId, ownerId, userId)
                .orElseGet(() -> {
                    ChatThread t = new ChatThread();
                    t.setPetId(petId);
                    t.setOwnerId(ownerId);
                    t.setUserId(userId);
                    return threadRepo.save(t);
                });
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> loadRecent(Long threadId, int limit) {
        ChatThread t = threadRepo.findById(threadId).orElseThrow();
        return msgRepo.findByThreadOrderBySentAtAsc(t, PageRequest.of(0, Math.max(1, limit))).getContent();
    }

    @Transactional(rollbackFor = {Exception.class})
    public ChatMessage persistMessage(Long threadId, Long senderId, String content) {
        String clean = content == null ? "" : content.trim();
        if (clean.isEmpty()) return null; // ignore empty messages

        ChatThread thread = threadRepo.findById(threadId).orElseThrow();

        // Save message first (NotificationService will push after TX commit)
        ChatMessage m = new ChatMessage();
        m.setThread(thread);
        m.setSenderId(senderId);
        m.setContent(clean);
        ChatMessage saved = msgRepo.save(m);

        // Determine recipient = the OTHER participant
        Long ownerId = thread.getOwnerId();
        Long userId = thread.getUserId();
        Long recipientId = senderId.equals(ownerId) ? userId : ownerId;

        // Guard: if somehow senderId equals recipientId or recipient is null, skip
        if (recipientId != null && !recipientId.equals(senderId)) {
            User sender = userRepository.findById(senderId).orElseThrow();
            String preview = sender.getUsername() + ": " + clean.substring(0, Math.min(30, clean.length()));

            events.chatMessageReceived(
                    /* senderUserId    */ senderId,
                    /* recipientUserId */ recipientId,
                    /* chatId          */ threadId,
                    /* preview         */ preview,
                    /* link            */ "/chats"
            );
        }

        return saved;
    }
}
