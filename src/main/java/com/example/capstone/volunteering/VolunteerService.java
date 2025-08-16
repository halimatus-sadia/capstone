package com.example.capstone.volunteering;

import com.example.capstone.auth.User;
import com.example.capstone.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolunteerService {
    
    private final VolunteerOpportunityRepository opportunityRepository;
    private final VolunteerApplicationRepository applicationRepository;
    private final VolunteerMapper volunteerMapper;
    private final AuthUtils authUtils;

    // ===== OPPORTUNITY METHODS =====
    
    public List<VolunteerOpportunityResponse> getAllOpportunities() {
        return opportunityRepository.findAll().stream()
                .map(this::mapOpportunityToResponse)
                .toList();
    }

    public VolunteerOpportunityResponse getOpportunityById(Long id) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer opportunity not found with id: " + id));
        return mapOpportunityToResponse(opportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public VolunteerOpportunityResponse createOpportunity(VolunteerOpportunityRequest request) {
        User currentUser = authUtils.getLoggedInUser();
        
        VolunteerOpportunity opportunity = volunteerMapper.toOpportunityEntity(request);
        opportunity.setPostedBy(currentUser);
        
        VolunteerOpportunity savedOpportunity = opportunityRepository.save(opportunity);
        return mapOpportunityToResponse(savedOpportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public VolunteerOpportunityResponse updateOpportunity(Long id, VolunteerOpportunityRequest request) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer opportunity not found with id: " + id));
        
        User currentUser = authUtils.getLoggedInUser();
        if (!opportunity.getPostedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update opportunities you posted");
        }
        
        volunteerMapper.updateOpportunityEntity(request, opportunity);
        VolunteerOpportunity updatedOpportunity = opportunityRepository.save(opportunity);
        return mapOpportunityToResponse(updatedOpportunity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOpportunity(Long id) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer opportunity not found with id: " + id));
        
        User currentUser = authUtils.getLoggedInUser();
        if (!opportunity.getPostedBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete opportunities you posted");
        }
        
        opportunityRepository.deleteById(id);
    }

    public List<VolunteerOpportunityResponse> getOpenOpportunities() {
        return opportunityRepository.findOpenOpportunitiesOrderByDate().stream()
                .map(this::mapOpportunityToResponse)
                .toList();
    }

    public List<VolunteerOpportunityResponse> getOpportunitiesByType(String type) {
        return opportunityRepository.findByType(type).stream()
                .map(this::mapOpportunityToResponse)
                .toList();
    }

    // ===== APPLICATION METHODS =====

    public List<VolunteerApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::mapApplicationToResponse)
                .toList();
    }

    public VolunteerApplicationResponse getApplicationById(Long id) {
        VolunteerApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer application not found with id: " + id));
        return mapApplicationToResponse(application);
    }

    @Transactional(rollbackFor = Exception.class)
    public VolunteerApplicationResponse createApplication(VolunteerApplicationRequest request) {
        User currentUser = authUtils.getLoggedInUser();
        
        // Check if opportunity exists and is open
        VolunteerOpportunity opportunity = opportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new RuntimeException("Volunteer opportunity not found with id: " + request.getOpportunityId()));
        
        if (!"open".equals(opportunity.getStatus())) {
            throw new RuntimeException("Cannot apply to closed opportunity");
        }
        
        // Check if user already applied
        if (applicationRepository.existsByUserIdAndOpportunityId(currentUser.getId(), request.getOpportunityId())) {
            throw new RuntimeException("You have already applied to this opportunity");
        }
        
        VolunteerApplication application = volunteerMapper.toApplicationEntity(request);
        application.setUser(currentUser);
        application.setOpportunity(opportunity);
        
        VolunteerApplication savedApplication = applicationRepository.save(application);
        return mapApplicationToResponse(savedApplication);
    }

    @Transactional(rollbackFor = Exception.class)
    public VolunteerApplicationResponse updateApplication(Long id, VolunteerApplicationRequest request) {
        VolunteerApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer application not found with id: " + id));
        
        User currentUser = authUtils.getLoggedInUser();
        if (!application.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only update your own applications");
        }
        
        volunteerMapper.updateApplicationEntity(request, application);
        VolunteerApplication updatedApplication = applicationRepository.save(application);
        return mapApplicationToResponse(updatedApplication);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteApplication(Long id) {
        VolunteerApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer application not found with id: " + id));
        
        User currentUser = authUtils.getLoggedInUser();
        if (!application.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own applications");
        }
        
        applicationRepository.deleteById(id);
    }

    public List<VolunteerApplicationResponse> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserId(userId).stream()
                .map(this::mapApplicationToResponse)
                .toList();
    }

    public List<VolunteerApplicationResponse> getApplicationsByOpportunityId(Long opportunityId) {
        return applicationRepository.findByOpportunityId(opportunityId).stream()
                .map(this::mapApplicationToResponse)
                .toList();
    }

    public List<VolunteerApplicationResponse> getCurrentUserApplications() {
        User currentUser = authUtils.getLoggedInUser();
        return getApplicationsByUserId(currentUser.getId());
    }

    // ===== PRIVATE MAPPING METHODS =====

    private VolunteerOpportunityResponse mapOpportunityToResponse(VolunteerOpportunity entity) {
        VolunteerOpportunityResponse response = volunteerMapper.toOpportunityResponse(entity);
        // Manually set the complex fields
        if (entity.getPostedBy() != null) {
            response.setPostedById(entity.getPostedBy().getId());
            response.setPostedByName(entity.getPostedBy().getName());
        }
        return response;
    }

    private VolunteerApplicationResponse mapApplicationToResponse(VolunteerApplication entity) {
        VolunteerApplicationResponse response = volunteerMapper.toApplicationResponse(entity);
        // Manually set the complex fields
        if (entity.getUser() != null) {
            response.setUserId(entity.getUser().getId());
            response.setUserName(entity.getUser().getName());
        }
        if (entity.getOpportunity() != null) {
            response.setOpportunityId(entity.getOpportunity().getId());
            response.setOpportunityTitle(entity.getOpportunity().getTitle());
        }
        return response;
    }
} 
