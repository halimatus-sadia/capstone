package com.example.capstone.pet.chat;

import lombok.Data;

@Data
public final class UserSummary {
    private final Long id;
    private final String name;
    private final String avatarUrl;

    public UserSummary(Long id, String name, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }
}