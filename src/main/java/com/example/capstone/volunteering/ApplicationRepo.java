package com.example.capstone.volunteering;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepo extends JpaRepository<VolunteerApplication, Long> {
} 
