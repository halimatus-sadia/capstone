package com.example.capstone.pet.chat;

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
        ChatMessage m = new ChatMessage();
        m.setThread(thread);
        m.setSenderId(senderId);
        m.setContent(clean);
        return msgRepo.save(m);
    }
}
