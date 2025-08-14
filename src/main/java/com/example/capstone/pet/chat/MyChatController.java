package com.example.capstone.pet.chat;

import com.example.capstone.auth.UserRepository;
import com.example.capstone.pet.PetRepository;
import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyChatController {

    private final ChatThreadRepository threadRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;
    private final PetRepository petRepository;
    private final AuthUtils authUtils;

    @GetMapping("/chats")
    public String myChats(Model model) {
        Long me = authUtils.getLoggedInUser().getId();

        var ownerPage = threadRepo.findByOwnerIdOrderByCreatedAtDesc(me, PageRequest.of(0, 200));
        var userPage = threadRepo.findByUserIdOrderByCreatedAtDesc(me, PageRequest.of(0, 200));

        List<Row> rows = new ArrayList<>();
        ownerPage.getContent().forEach(t -> rows.add(buildRow(t, me, true)));
        userPage.getContent().forEach(t -> rows.add(buildRow(t, me, false)));

        rows.sort(Comparator.comparing(Row::lastAt).reversed());
        model.addAttribute("rows", rows);
        model.addAttribute("currentUserId", me);
        return "pet/chat/my_chat";
    }

    private Row buildRow(ChatThread t, Long me, boolean iAmOwner) {
        Long otherId = iAmOwner ? t.getUserId() : t.getOwnerId();

        var other = userRepo.findSummaryById(otherId).orElse(new UserSummary(otherId, "User " + otherId, null));
        var pet = petRepository.findSummaryById(t.getPetId())
                .map(p -> new PetSummary(p.id(), p.name(), p.imageUrl()))
                .orElse(new PetSummary(t.getPetId(), "Pet " + t.getPetId(), null));

        var last = msgRepo.findTop1ByThreadOrderBySentAtDesc(t);
        String preview = (last != null) ? last.getContent() : "";
        LocalDateTime lastAt = (last != null) ? last.getSentAt() : t.getCreatedAt();

        return new Row(
                t.getId(),
                iAmOwner ? "OWNER" : "USER",
                pet.id(), pet.name(), pet.imageUrl(),
                other.getId(), other.getName(), other.getAvatarUrl(),
                preview, lastAt
        );
    }

    public record Row(
            Long threadId,
            String role,
            Long petId, String petName, String petImageUrl,
            Long otherUserId, String otherName, String otherAvatarUrl,
            String lastMessage, LocalDateTime lastAt
    ) {
    }
}
