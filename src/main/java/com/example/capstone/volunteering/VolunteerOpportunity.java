package com.example.capstone.volunteering;

import com.example.capstone.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteer_opportunity")
public class VolunteerOpportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "location")
    private String location;

    @Column(name = "type")
    private String type; // rescue, foster, event help, etc.

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "status")
    private String status; // open, closed, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;
} 
