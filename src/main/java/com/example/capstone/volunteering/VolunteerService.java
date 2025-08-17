package com.example.capstone.volunteering;

import com.example.capstone.auth.User;
import com.example.capstone.auth.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class VolunteerService {

    private final VolunteerOpportunityRepository opportunityRepository;
    private final VolunteerApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final VolunteerMapper mapper;

    public VolunteerService(VolunteerOpportunityRepository opportunityRepository,
                            VolunteerApplicationRepository applicationRepository,
                            UserRepository userRepository,
                            VolunteerMapper mapper) {
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Page<VolunteerOpportunity> listOpportunities(String keyword, LocalDate from, LocalDate to,
                                                        VolunteerOpportunityStatus status, Pageable pageable) {
        String k = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return opportunityRepository.search(k, from, to, status, pageable);
    }

    @Transactional
    public VolunteerOpportunity createOpportunity(String creatorUsername, @Valid VolunteerOpportunityRequest req) {
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + creatorUsername));
        VolunteerOpportunity o = new VolunteerOpportunity();
        o.setTitle(req.getTitle());
        o.setDescription(req.getDescription());
        o.setLocation(req.getLocation());
        o.setStartDate(req.getStartDate());
        o.setEndDate(req.getEndDate());
        o.setMaxVolunteers(req.getMaxVolunteers());
        o.setStatus(VolunteerOpportunityStatus.OPEN);
        o.setCreatedBy(creator);
        return opportunityRepository.save(o);
    }

    @Transactional
    public VolunteerOpportunity updateOpportunity(Long id, String requesterUsername, @Valid VolunteerOpportunityRequest req) {
        VolunteerOpportunity o = opportunityRepository.findByIdWithCreator(id)
                .orElseThrow(() -> new EntityNotFoundException("Opportunity not found: " + id));
        if (!o.getCreatedBy().getUsername().equals(requesterUsername)) {
            throw new SecurityException("Only the creator can update this opportunity");
        }
        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        o.setTitle(req.getTitle());
        o.setDescription(req.getDescription());
        o.setLocation(req.getLocation());
        o.setStartDate(req.getStartDate());
        o.setEndDate(req.getEndDate());
        o.setMaxVolunteers(req.getMaxVolunteers());
        return opportunityRepository.save(o);
    }

    @Transactional
    public VolunteerOpportunity getOpportunity(Long id) {
        return opportunityRepository.findByIdWithCreator(id)
                .orElseThrow(() -> new EntityNotFoundException("Opportunity not found: " + id));
    }

    @Transactional
    public void openOpportunity(Long id, String requesterUsername) {
        VolunteerOpportunity o = getOpportunity(id);
        if (!o.getCreatedBy().getUsername().equals(requesterUsername)) {
            throw new SecurityException("Only the creator can open this opportunity");
        }
        o.setStatus(VolunteerOpportunityStatus.OPEN);
        opportunityRepository.save(o);
    }

    @Transactional
    public void closeOpportunity(Long id, String requesterUsername) {
        VolunteerOpportunity o = getOpportunity(id);
        if (!o.getCreatedBy().getUsername().equals(requesterUsername)) {
            throw new SecurityException("Only the creator can close this opportunity");
        }
        o.setStatus(VolunteerOpportunityStatus.CLOSED);
        opportunityRepository.save(o);
    }

    @Transactional
    public VolunteerApplication apply(Long opportunityId, String username, @Valid VolunteerApplicationRequest req) {
        VolunteerOpportunity o = getOpportunity(opportunityId);
        if (o.getStatus() != VolunteerOpportunityStatus.OPEN) {
            throw new IllegalStateException("Opportunity is not open");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (applicationRepository.existsByApplicantAndOpportunity(user, o)) {
            throw new IllegalStateException("You have already applied");
        }

        if (o.getMaxVolunteers() != null) {
            long approved = applicationRepository.countByOpportunityAndStatus(o, VolunteerApplicationStatus.APPROVED);
            if (approved >= o.getMaxVolunteers()) {
                throw new IllegalStateException("Capacity full");
            }
        }

        VolunteerApplication a = new VolunteerApplication();
        a.setOpportunity(o);
        a.setApplicant(user);
        a.setMotivation(req.getMotivation());
        a.setStatus(VolunteerApplicationStatus.PENDING);
        return applicationRepository.save(a);
    }

    @Transactional
    public void withdraw(Long applicationId, String username) {
        VolunteerApplication a = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found: " + applicationId));
        if (!a.getApplicant().getUsername().equals(username)) {
            throw new SecurityException("You can withdraw only your own application");
        }
        a.setStatus(VolunteerApplicationStatus.CANCELLED);
        applicationRepository.save(a);
    }

    @Transactional
    public void decide(Long applicationId, String requesterUsername, boolean approve) {
        VolunteerApplication a = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application not found: " + applicationId));
        VolunteerOpportunity o = a.getOpportunity();
        if (!o.getCreatedBy().getUsername().equals(requesterUsername)) {
            throw new SecurityException("Only the creator can decide on applications");
        }
        if (approve) {
            if (o.getMaxVolunteers() != null) {
                long approved = applicationRepository.countByOpportunityAndStatus(o, VolunteerApplicationStatus.APPROVED);
                if (approved >= o.getMaxVolunteers()) {
                    throw new IllegalStateException("Capacity full");
                }
            }
            a.setStatus(VolunteerApplicationStatus.APPROVED);
        } else {
            a.setStatus(VolunteerApplicationStatus.REJECTED);
        }
        applicationRepository.save(a);
    }
}
