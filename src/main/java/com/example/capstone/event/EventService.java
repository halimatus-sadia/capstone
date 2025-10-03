package com.example.capstone.event;

import com.example.capstone.auth.User;
import com.example.capstone.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Page<Event> list(String keyword, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        String k = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return eventRepository.search(k, from, to, pageable);
    }

    @Transactional
    public Event create(String creatorUsername, @Valid EventRequest req) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new EntityNotFoundException("Creator not found: " + creatorUsername));

        if (req.getEndDateTime().isBefore(req.getStartDateTime())) {
            throw new IllegalArgumentException("endDateTime must be after startDateTime");
        }

        Event e = new Event();
        e.setTitle(req.getTitle());
        e.setDescription(req.getDescription());
        e.setLocation(req.getLocation());
        e.setStartDateTime(req.getStartDateTime());
        e.setEndDateTime(req.getEndDateTime());
        e.setImageUrl(req.getImageUrl());
        e.setCreatedBy(creator);
        return eventRepository.save(e);
    }

    @Transactional
    public Event update(Long eventId, String requesterUsername, @Valid EventRequest req) {
        Event e = eventRepository.findByIdWithParticipants(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + eventId));

        if (!e.getCreatedBy().getUsername().equals(requesterUsername)) {
            throw new SecurityException("Only the creator can update this event");
        }
        if (req.getEndDateTime().isBefore(req.getStartDateTime())) {
            throw new IllegalArgumentException("endDateTime must be after startDateTime");
        }

        e.setTitle(req.getTitle());
        e.setDescription(req.getDescription());
        e.setLocation(req.getLocation());
        e.setStartDateTime(req.getStartDateTime());
        e.setEndDateTime(req.getEndDateTime());
        e.setImageUrl(req.getImageUrl());
        return eventRepository.save(e);
    }

    public Event get(Long id) {
        return eventRepository.findByIdWithParticipants(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found: " + id));
    }

    @Transactional
    public void toggleGoing(Long eventId, String username) {
        Event e = get(eventId);
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (e.getGoingUsers().contains(u)) {
            e.getGoingUsers().remove(u);
        } else {
            e.getGoingUsers().add(u);
            // Mutually exclusive with interested
            e.getInterestedUsers().remove(u);
        }
        eventRepository.save(e);
    }

    @Transactional
    public void toggleInterested(Long eventId, String username) {
        Event e = get(eventId);
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (e.getInterestedUsers().contains(u)) {
            e.getInterestedUsers().remove(u);
        } else {
            e.getInterestedUsers().add(u);
            // Mutually exclusive with going
            e.getGoingUsers().remove(u);
        }
        eventRepository.save(e);
    }

    @Transactional
    public void toggleNotify(Long eventId, String username) {
        Event e = get(eventId);
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (e.getNotifyUsers().contains(u)) {
            e.getNotifyUsers().remove(u);
        } else {
            e.getNotifyUsers().add(u);
        }
        eventRepository.save(e);
    }
}

