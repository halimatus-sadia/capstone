package com.example.capstone.pet.chat;

import com.example.capstone.pet.PetRepository;
import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService service;
    private final AuthUtils authUtils;
    private final PetRepository petRepository;

    // Create/find the unique thread for this pet + (viewer↔owner)
    @PostMapping("/thread")
    public ChatDtos.ThreadInitResponse startThread(
            @RequestBody ChatDtos.ThreadInitRequest req) {
        // You will need to fetch ownerId from the pet or controller model.
        // If pet owner id is already rendered in page (data-*), the client can pass it too.
        Long ownerId = petRepository.findById(req.petId())
                .orElseThrow()
                .getOwner().getId();
        Long userId = authUtils.getLoggedInUser().getId();

        ChatThread t = service.getOrCreateThread(req.petId(), ownerId, userId);
        return new ChatDtos.ThreadInitResponse(t.getId(), t.getPetId(), t.getOwnerId(), t.getUserId());
    }

    // Load initial history
    @GetMapping("/{threadId}/messages")
    public List<ChatDtos.MessageView> history(@PathVariable Long threadId,
                                              @RequestParam(defaultValue = "50") int limit) {
        return service.loadRecent(threadId, limit)
                .stream()
                .map(m -> new ChatDtos.MessageView(m.getId(), m.getSenderId(), m.getContent(), m.getSentAt()))
                .toList();
    }
}
