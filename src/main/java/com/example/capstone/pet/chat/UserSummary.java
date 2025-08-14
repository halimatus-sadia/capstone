package com.example.capstone.pet.chat;

import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public final class UserSummary {
    private final Long id;
    private final String name;
    private final String avatarUrl;

    public UserSummary(Long id, String name, String avatarUrl) {
        this.id = id;
        this.name = name;
        if (StringUtils.hasText(avatarUrl)) {
            this.avatarUrl = "http://localhost:9000/capstone/" + avatarUrl;
        } else {
            this.avatarUrl = "http://localhost:8080/images/avatar.png";
        }
    }
}