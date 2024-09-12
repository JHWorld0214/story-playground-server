//package com.softgallery.story_playground_server.repository.story;
//
//import com.softgallery.story_playground_server.entity.PageEntity;
//import com.softgallery.story_playground_server.entity.StoryEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface PageRepository extends JpaRepository<PageEntity, Long> {
//    Optional<PageEntity> findFirstByStoryOrderByPageIndexDesc(StoryEntity storyEntity);
//    List<PageEntity> findAllByStoryOrderByPageIndex(StoryEntity storyEntity);
//}
