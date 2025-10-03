package com.example.capstone.volunteering;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VolunteerApplicationRequest {

    @NotBlank
    @Size(max = 4000)
    private String motivation;

    public String getMotivation() { return motivation; }
    public void setMotivation(String motivation) { this.motivation = motivation; }
}
