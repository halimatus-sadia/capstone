package com.example.capstone.pet.chat;

import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompController {
    private final ChatService service;
    private final SimpMessagingTemplate template;
    private final AuthUtils authUtils;

    // Client sends to /app/chat.send
    @MessageMapping("/chat.send")
    public void onSend(ChatDtos.SendMessage payload) {
        Long senderId = payload.senderId();
        if (senderId == null) {
            // fallback if you later wire WS auth:
            var u = authUtils.getLoggedInUser();
            senderId = u.getId();
        }
        if (payload.threadId() == null || senderId == null || payload.content() == null) return;

        ChatMessage saved = service.persistMessage(payload.threadId(), senderId, payload.content());
        if (saved == null) return; // was empty/whitespace

        ChatDtos.MessageView view =
                new ChatDtos.MessageView(saved.getId(), saved.getSenderId(), saved.getContent(), saved.getSentAt());
        template.convertAndSend("/topic/chats." + payload.threadId(), view);
    }
}
