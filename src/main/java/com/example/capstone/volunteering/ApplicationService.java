package com.example.capstone.volunteering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepo applicationRepository;

    public List<VolunteerApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Optional<VolunteerApplication> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public VolunteerApplication createApplication(VolunteerApplication application) {
        return applicationRepository.save(application);
    }

    public VolunteerApplication updateApplication(Long id, VolunteerApplication details) {
        return applicationRepository.findById(id).map(application -> {
            application.setUser(details.getUser());
            application.setOpportunity(details.getOpportunity());
            application.setStatus(details.getStatus());
            return applicationRepository.save(application);
        }).orElseGet(() -> {
            details.setId(id);
            return applicationRepository.save(details);
        });
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
} 
