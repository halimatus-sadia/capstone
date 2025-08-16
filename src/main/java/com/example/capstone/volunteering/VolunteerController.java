package com.example.capstone.volunteering;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer")
@RequiredArgsConstructor
public class VolunteerController {
    
    private final VolunteerService volunteerService;

    // ===== OPPORTUNITY ENDPOINTS =====
    
    @GetMapping("/opportunities")
    public ResponseEntity<List<VolunteerOpportunityResponse>> getAllOpportunities() {
        List<VolunteerOpportunityResponse> opportunities = volunteerService.getAllOpportunities();
        return ResponseEntity.ok(opportunities);
    }

    @GetMapping("/opportunities/{id}")
    public ResponseEntity<VolunteerOpportunityResponse> getOpportunityById(@PathVariable Long id) {
        try {
            VolunteerOpportunityResponse opportunity = volunteerService.getOpportunityById(id);
            return ResponseEntity.ok(opportunity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/opportunities")
    public ResponseEntity<VolunteerOpportunityResponse> createOpportunity(@Valid @RequestBody VolunteerOpportunityRequest request) {
        try {
            VolunteerOpportunityResponse opportunity = volunteerService.createOpportunity(request);
            return ResponseEntity.ok(opportunity);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/opportunities/{id}")
    public ResponseEntity<VolunteerOpportunityResponse> updateOpportunity(@PathVariable Long id, @Valid @RequestBody VolunteerOpportunityRequest request) {
        try {
            VolunteerOpportunityResponse opportunity = volunteerService.updateOpportunity(id, request);
            return ResponseEntity.ok(opportunity);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/opportunities/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        try {
            volunteerService.deleteOpportunity(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/opportunities/open")
    public ResponseEntity<List<VolunteerOpportunityResponse>> getOpenOpportunities() {
        List<VolunteerOpportunityResponse> opportunities = volunteerService.getOpenOpportunities();
        return ResponseEntity.ok(opportunities);
    }

    @GetMapping("/opportunities/type/{type}")
    public ResponseEntity<List<VolunteerOpportunityResponse>> getOpportunitiesByType(@PathVariable String type) {
        List<VolunteerOpportunityResponse> opportunities = volunteerService.getOpportunitiesByType(type);
        return ResponseEntity.ok(opportunities);
    }

    // ===== APPLICATION ENDPOINTS =====

    @GetMapping("/applications")
    public ResponseEntity<List<VolunteerApplicationResponse>> getAllApplications() {
        List<VolunteerApplicationResponse> applications = volunteerService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<VolunteerApplicationResponse> getApplicationById(@PathVariable Long id) {
        try {
            VolunteerApplicationResponse application = volunteerService.getApplicationById(id);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/applications")
    public ResponseEntity<VolunteerApplicationResponse> createApplication(@Valid @RequestBody VolunteerApplicationRequest request) {
        try {
            VolunteerApplicationResponse application = volunteerService.createApplication(request);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/applications/{id}")
    public ResponseEntity<VolunteerApplicationResponse> updateApplication(@PathVariable Long id, @Valid @RequestBody VolunteerApplicationRequest request) {
        try {
            VolunteerApplicationResponse application = volunteerService.updateApplication(id, request);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        try {
            volunteerService.deleteApplication(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/applications/user/{userId}")
    public ResponseEntity<List<VolunteerApplicationResponse>> getApplicationsByUserId(@PathVariable Long userId) {
        List<VolunteerApplicationResponse> applications = volunteerService.getApplicationsByUserId(userId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/opportunity/{opportunityId}")
    public ResponseEntity<List<VolunteerApplicationResponse>> getApplicationsByOpportunityId(@PathVariable Long opportunityId) {
        List<VolunteerApplicationResponse> applications = volunteerService.getApplicationsByOpportunityId(opportunityId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/my-applications")
    public ResponseEntity<List<VolunteerApplicationResponse>> getCurrentUserApplications() {
        List<VolunteerApplicationResponse> applications = volunteerService.getCurrentUserApplications();
        return ResponseEntity.ok(applications);
    }
} 