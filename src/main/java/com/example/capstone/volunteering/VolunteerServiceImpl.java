package com.example.capstone.volunteering;

import com.example.capstone.auth.User;        // ✅ correct package
import com.example.capstone.auth.UserService; // ✅ correct package
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerOpportunityRepository opportunityRepository;
    private final VolunteerApplicationRepository applicationRepository;
    private final VolunteerMapper mapper;
    private final UserService userService;

    @Override
    public List<VolunteerOpportunityResponse> getAllOpportunities() {
        return opportunityRepository.findAll()
                .stream()
                .map(mapper::toOpportunityResponse)
                .toList();
    }

    @Override
    public VolunteerOpportunityResponse createOpportunity(VolunteerOpportunityRequest request) {
        User currentUser = userService.getCurrentUser();
        VolunteerOpportunity opportunity = mapper.toOpportunity(request);
        opportunity.setCreatedBy(currentUser);
        VolunteerOpportunity saved = opportunityRepository.save(opportunity);
        return mapper.toOpportunityResponse(saved);
    }

    @Override
    public VolunteerOpportunityResponse getOpportunityById(Long id) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));
        return mapper.toOpportunityResponse(opportunity);
    }

    @Override
    public void applyToOpportunity(Long opportunityId) {
        User currentUser = userService.getCurrentUser();
        VolunteerOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));

        Optional<VolunteerApplication> existing =
                applicationRepository.findByUserAndOpportunity(currentUser, opportunity);
        if (existing.isPresent()) {
            throw new IllegalStateException("You already applied to this opportunity");
        }

        VolunteerApplication application = VolunteerApplication.builder()
                .user(currentUser)
                .opportunity(opportunity)
                .status(ApplicationStatus.PENDING)
                .build();

        applicationRepository.save(application);
    }

    @Override
    public List<VolunteerApplicationResponse> getMyApplications() {
        User currentUser = userService.getCurrentUser();
        return applicationRepository.findByUser(currentUser)
                .stream()
                .map(mapper::toApplicationResponse)
                .toList();
    }

    @Override
    public void decide(Long applicationId, boolean approve) {
        VolunteerApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        app.setStatus(approve ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED);
        applicationRepository.save(app);
    }
}

