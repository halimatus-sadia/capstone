package com.example.capstone.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repo;
    private final SimpMessagingTemplate broker;

    /**
     * Create notification and push AFTER the transaction commits to avoid
     * the "push first, then fetch stale DB" race on the client.
     */
    @Transactional
    public Notification create(Notification n) {
        // Skip if no recipient provided (defensive)
        if (n.getRecipientId() == null) {
            return n;
        }

        Notification saved = repo.save(n);

        Long uid = saved.getRecipientId();
        String dest = "/topic/notifications/" + uid;

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                broker.convertAndSend(dest, saved);
            }
        });

        return saved;
    }


    public List<Notification> recentFor(Long userId) {
        return repo.findTop20ByRecipientIdOrderByCreatedAtDesc(userId);
    }

    public long unreadCount(Long userId) {
        return repo.countByRecipientIdAndReadFlagFalse(userId);
    }

    @Transactional
    public void markRead(Long id, Long userId) {
        repo.findById(id).ifPresent(n -> {
            if (userId.equals(n.getRecipientId())) {
                n.setReadFlag(true);
            }
        });
    }

    @Transactional
    public void markAllRead(Long userId) {
        repo.findTop20ByRecipientIdOrderByCreatedAtDesc(userId)
                .forEach(n -> n.setReadFlag(true));
    }
}
