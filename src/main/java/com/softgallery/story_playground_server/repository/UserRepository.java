package com.softgallery.story_playground_server.repository;

import com.softgallery.story_playground_server.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByUserId(UUID userId);
}
