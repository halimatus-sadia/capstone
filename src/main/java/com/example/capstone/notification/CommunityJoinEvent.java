// src/main/java/com/example/capstone/notification/CommunityJoinEvent.java
package com.example.capstone.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityJoinEvent {
    private final Long actorUserId; // who joined/left
    private final Long communityId;
    private final boolean joined;   // true = joined, false = left
    private final String link;      // e.g. "/communities/{id}"
}
