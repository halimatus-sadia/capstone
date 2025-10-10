package com.example.capstone.notification;

import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;
    private final AuthUtils authUtils;

    private Long currentUserId(Principal principal) {
        return authUtils.getLoggedInUser().getId();
    }

    @GetMapping
    public ResponseEntity<List<Notification>> recent(Principal principal) {
        var list = service.recentFor(currentUserId(principal));
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore()) // prevent stale cache
                .body(list);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount(Principal principal) {
        long c = service.unreadCount(currentUserId(principal));
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore()) // prevent stale cache
                .body(c);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id, Principal principal) {
        service.markRead(id, currentUserId(principal));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAll(Principal principal) {
        service.markAllRead(currentUserId(principal));
        return ResponseEntity.ok().build();
    }
}
