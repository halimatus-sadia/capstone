package com.example.capstone.heathlcareinfo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthCareInfoController {
    @GetMapping(value = "/healthcare-centers")
    public String index() {
        return "healthcare/healthcare";
    }
}
