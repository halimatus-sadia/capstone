package com.example.capstone.auth;

import com.example.capstone.pet.chat.UserSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsernameEqualsIgnoreCase(String username);

    boolean existsByUsernameEqualsIgnoreCaseAndIdNot(String username, Long id);

    @Query("select new com.example.capstone.pet.chat.UserSummary(u.id, coalesce(u.name, concat('User ', u.id)), u.profileImageFilePath) from User u where u.id=:id")
    Optional<UserSummary> findSummaryById(Long id);
}

