package com.example.capstone.volunteering;

import com.example.capstone.auth.User;
import com.example.capstone.auth.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VolunteeringServiceImpl implements VolunteeringService {

    private final VolunteerOpportunityRepository opportunityRepo;
    private final VolunteerApplicationRepository applicationRepo;
    private final UserRepository userRepository;
    private final VolunteerMapper mapper;

    public VolunteeringServiceImpl(VolunteerOpportunityRepository opportunityRepo,
                                   VolunteerApplicationRepository applicationRepo,
                                   UserRepository userRepository,
                                   VolunteerMapper mapper) {
        this.opportunityRepo = opportunityRepo;
        this.applicationRepo = applicationRepo;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<VolunteerOpportunityResponse> findAll(String location, VolunteerOpportunityType type, LocalDate date, Pageable pageable) {
        // For now ignore filters and return all; repository search signature differs
        return opportunityRepo.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    public VolunteerOpportunityResponse getById(Long id) {
        VolunteerOpportunity o = opportunityRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + id));
        return mapper.toResponse(o);
    }

    @Override
    public void create(VolunteerOpportunityRequest form, String username) {
        if (form.getStartDate() != null && form.getEndDate() != null
                && form.getEndDate().isBefore(form.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        VolunteerOpportunity entity = new VolunteerOpportunity();
        entity.setTitle(form.getTitle());
        entity.setDescription(form.getDescription());
        entity.setLocation(form.getLocation());
        entity.setType(form.getType());
        entity.setStartDate(form.getStartDate());
        entity.setEndDate(form.getEndDate());
        entity.setStatus(form.getStatus());
        entity.setMaxVolunteers(form.getMaxVolunteers());
        entity.setRequirements(form.getRequirements());
        entity.setContactInfo(form.getContactInfo());
        entity.setCreatedBy(creator);

        opportunityRepo.save(entity);
    }

    @Override
    public void delete(Long id, String username) {
        VolunteerOpportunity o = opportunityRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + id));
        if (!(isOwner(o, username) || isAdmin(username))) {
            throw new AccessDeniedException("Not authorized to delete this opportunity");
        }
        opportunityRepo.delete(o);
    }

    @Override
    public void apply(Long opportunityId, String username) {
        VolunteerOpportunity o = opportunityRepo.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));

        User applicant = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        if (alreadyApplied(opportunityId, username)) {
            throw new IllegalStateException("You already applied to this opportunity");
        }
        if (o.getStatus() != VolunteerOpportunityStatus.OPEN) {
            throw new IllegalStateException("This opportunity is not open for applications");
        }

        VolunteerApplication app = new VolunteerApplication();
        app.setOpportunity(o);
        app.setApplicant(applicant);
        app.setMotivation("");
        app.setStatus(VolunteerApplicationStatus.PENDING);

        applicationRepo.save(app);
    }

    @Override
    public boolean alreadyApplied(Long opportunityId, String username) {
        VolunteerOpportunity o = opportunityRepo.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));
        User applicant = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return applicationRepo.existsByApplicantAndOpportunity(applicant, o);
    }

    @Override
    public void withdraw(Long applicationId, String username) {
        VolunteerApplication app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + applicationId));
        if (!username.equals(app.getApplicant().getUsername())) {
            throw new AccessDeniedException("Not your application");
        }
        applicationRepo.delete(app);
    }

    @Override
    public boolean isOwner(Long opportunityId, String username) {
        VolunteerOpportunity o = opportunityRepo.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + opportunityId));
        return isOwner(o, username);
    }

    private boolean isOwner(VolunteerOpportunity o, String username) {
        return username != null && o.getCreatedBy() != null && username.equals(o.getCreatedBy().getUsername());
    }

    @Override
    public boolean isAdmin(String username) {
        // Hook into real roles if needed
        return false;
    }

    @Override
    public List<VolunteerApplicationResponse> getMyApplications(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return applicationRepo.findByApplicant(u, Pageable.unpaged())
                .map(mapper::toResponse)
                .getContent();
    }
}
