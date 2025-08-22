package com.example.capstone.pet.chat;

import com.example.capstone.auth.User;
import com.example.capstone.auth.UserRepository;
import com.example.capstone.common.MinIOService;
import com.example.capstone.pet.Pet;
import com.example.capstone.pet.PetRepository;
import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class MyChatController {
    private final ChatThreadRepository threadRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;
    private final PetRepository petRepository;
    private final AuthUtils authUtils;
    private final MinIOService minIOService;

    @GetMapping("/chats")
    public String myChats(Model model) {
        Long me = authUtils.getLoggedInUser().getId();

        var ownerPage = threadRepo.findByOwnerIdOrderByCreatedAtDesc(me, Pageable.unpaged());
        var userPage = threadRepo.findByUserIdOrderByCreatedAtDesc(me, Pageable.unpaged());

        List<Row> rows = new ArrayList<>();
        ownerPage.getContent().forEach(t -> rows.add(buildRow(t, me, true)));
        userPage.getContent().forEach(t -> rows.add(buildRow(t, me, false)));

        rows.removeIf(Objects::isNull);

        rows.sort(Comparator.comparing(Row::lastAt).reversed());
        model.addAttribute("rows", rows);
        model.addAttribute("currentUserId", me);
        return "pet/chat/my_chat";
    }

    private Row buildRow(ChatThread t, Long me, boolean iAmOwner) {
        Long otherId = iAmOwner ? t.getUserId() : t.getOwnerId();

        // --- Other user (safe fallbacks) ---
        User other = userRepo.findById(otherId).orElse(null);
        String otherName = (other != null && other.getName() != null) ? other.getName() : ("user" + otherId);
        String otherAvatar = (other != null && StringUtils.hasText(other.getProfileImageFilePath())) ?
                minIOService.constructFullUrl(other.getProfileImageFilePath()) : null;

        // --- Pet (safe fallbacks) ---
        Pet pet = petRepository.findById(t.getPetId()).orElse(null);
        String petName = (pet != null && pet.getName() != null) ? pet.getName() : ("Pet " + t.getPetId());
        String petImage = (pet != null) ? "image" : null; // TODO: handle image

        var last = msgRepo.findTop1ByThreadOrderBySentAtDesc(t);
        if (last == null) {
            return null;
        }
        String preview = last.getContent();
        LocalDateTime lastAt = last.getSentAt();

        return new Row(
                t.getId(),
                petName, petImage,
                otherId, otherName, otherAvatar,
                preview, lastAt
        );
    }

    public record Row(
            Long threadId,
            String petName, String petImageUrl,
            Long otherUserId, String otherName, String otherAvatarUrl,
            String lastMessage, LocalDateTime lastAt
    ) {
    }
}
