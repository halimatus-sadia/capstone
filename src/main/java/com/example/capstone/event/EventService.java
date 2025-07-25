package com.example.capstone.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(eventDetails.getTitle());
            event.setDescription(eventDetails.getDescription());
            event.setLocation(eventDetails.getLocation());
            event.setDateTime(eventDetails.getDateTime());
            event.setType(eventDetails.getType());
            event.setOrganizer(eventDetails.getOrganizer());
            return eventRepository.save(event);
        }).orElseGet(() -> {
            eventDetails.setId(id);
            return eventRepository.save(eventDetails);
        });
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
} 
