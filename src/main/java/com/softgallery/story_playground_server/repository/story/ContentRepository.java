package com.softgallery.story_playground_server.repository.story;

import com.softgallery.story_playground_server.entity.ContentEntity;
import com.softgallery.story_playground_server.entity.StoryEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
//    List<ContentEntity> findAllByPageOrderByCreatedDate(PageEntity pageEntity);

    List<ContentEntity> findAllByStoryOrderByCreatedDate(StoryEntity storyEntity);
}
