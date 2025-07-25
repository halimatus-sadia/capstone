package com.example.capstone.volunteering;

import com.example.capstone.auth.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "volunteer_opportunity")
public class VolunteerOpportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    private String type; // rescue, foster, event help, etc.

    private LocalDate date;

    private String status; // open, closed, etc.

    @ManyToOne
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;
} 