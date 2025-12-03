// src/main/java/com/petport/home/HomeService.java
package com.example.capstone.home;

import com.example.capstone.community.CommunityPost;
import com.example.capstone.community.CommunityPostRepository;
import com.example.capstone.event.Event;
import com.example.capstone.event.EventRepository;
import com.example.capstone.pet.Pet;
import com.example.capstone.pet.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final PetRepository petRepository;
    private final CommunityPostRepository postRepository;
    private final EventRepository eventRepository;

    public List<Pet> latestPets() {
        return petRepository.findTop6ByOrderByCreatedAtDesc();
    }

    public List<CommunityPost> latestPosts() {
        return postRepository.findTop6ByOrderByCreatedAtDesc();
    }

    public List<Event> upcomingEvents() {
        return eventRepository.findTop6ByStartDateTimeAfterOrderByStartDateTimeAsc(LocalDateTime.now());
    }
}
