package com.softgallery.story_playground_server.service.story;

import com.softgallery.story_playground_server.dto.content.ContentInsertDTO;
import com.softgallery.story_playground_server.dto.content.ContentOnlyDTO;
import com.softgallery.story_playground_server.dto.page.PageIdDTO;
import com.softgallery.story_playground_server.dto.story.StoryIdDTO;
import com.softgallery.story_playground_server.entity.ContentEntity;
import com.softgallery.story_playground_server.entity.PageEntity;
import com.softgallery.story_playground_server.entity.StoryEntity;
import com.softgallery.story_playground_server.entity.UserEntity;
import com.softgallery.story_playground_server.global.error.exception.EntityNotFoundException;
import com.softgallery.story_playground_server.repository.UserRepository;
import com.softgallery.story_playground_server.repository.story.ContentRepository;
import com.softgallery.story_playground_server.repository.story.PageRepository;
import com.softgallery.story_playground_server.repository.story.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class StoryService {
    private final PageRepository pageRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public Long saveMessage(ContentInsertDTO contentInsertDTO) {
        Optional<PageEntity> safePage = pageRepository.findById(contentInsertDTO.getPageId());
        if(safePage.isEmpty()) throw new EntityNotFoundException();

        ContentEntity content = contentRepository.save(
                ContentEntity.builder()
                        .createdDate(LocalDateTime.now())
                        .content(contentInsertDTO.getContent())
                        .page(safePage.get())
                        .build()
        );

        return content.getContentId();
    }

    public StoryIdDTO makeNewStory(UUID userId) {
        Optional<UserEntity> safeUser = userRepository.findById(userId);
        if(safeUser.isEmpty()) throw new EntityNotFoundException();

        StoryEntity storyEntity = storyRepository.save(
                StoryEntity.builder()
                        .user(safeUser.get())
                        .title(StoryConfig.DEFAULT_TITLE)
                        .isCompleted(StoryConfig.DEFAULT_COMPLETE)
                        .createdDate(LocalDateTime.now())
                        .modifiedDate(LocalDateTime.now())
                        .build()
        );

        return new StoryIdDTO(storyEntity.getStoryId());
    }

    public PageIdDTO makeNewPage(UUID userId, StoryIdDTO storyIdDTO) {
        Optional<UserEntity> safeUser = userRepository.findById(userId);
        if(safeUser.isEmpty()) throw new EntityNotFoundException();

        Optional<StoryEntity> safeStory = storyRepository.findById(storyIdDTO.getStoryId());
        if(safeStory.isEmpty()) throw new EntityNotFoundException();

        Long currPageIndex=-1L;
        Optional<PageEntity> safeLargestPage = pageRepository.findFirstByStoryOrderByPageIndexDesc(safeStory.get());
        if(safeLargestPage.isEmpty()) currPageIndex = 1L;
        else currPageIndex = safeLargestPage.get().getPageIndex();

        PageEntity pageEntity = pageRepository.save(
                PageEntity.builder()
                        .pageIndex(currPageIndex)
                        .story(safeStory.get())
                        .build()
        );

        return new PageIdDTO(pageEntity.getPageId());
    }

    public ContentOnlyDTO receiveMessage(Long storyId) {

    }
}
