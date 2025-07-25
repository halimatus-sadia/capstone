package com.example.capstone.volunteering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer-opportunities")
public class OpportunityCtrl {
    @Autowired
    private OpportunityService opportunityService;

    @GetMapping
    public List<VolunteerOpportunity> getAllOpportunities() {
        return opportunityService.getAllOpportunities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerOpportunity> getOpportunityById(@PathVariable Long id) {
        return opportunityService.getOpportunityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public VolunteerOpportunity createOpportunity(@RequestBody VolunteerOpportunity opportunity) {
        return opportunityService.createOpportunity(opportunity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerOpportunity> updateOpportunity(@PathVariable Long id, @RequestBody VolunteerOpportunity opportunity) {
        return ResponseEntity.ok(opportunityService.updateOpportunity(id, opportunity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        opportunityService.deleteOpportunity(id);
        return ResponseEntity.noContent().build();
    }
} 
