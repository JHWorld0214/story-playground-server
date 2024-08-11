package com.softgallery.story_playground_server.repository;

import com.softgallery.story_playground_server.entity.StoryEvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryEvaluationRepository extends JpaRepository<StoryEvaluationEntity, Long> {
    boolean existsByUsernameAndStoryId(String username, Long storyId);
}
