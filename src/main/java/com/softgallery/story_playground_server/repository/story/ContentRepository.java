package com.softgallery.story_playground_server.repository.story;

import com.softgallery.story_playground_server.entity.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
}
