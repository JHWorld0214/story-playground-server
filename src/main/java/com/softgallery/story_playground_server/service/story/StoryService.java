package com.softgallery.story_playground_server.service.story;

import com.softgallery.story_playground_server.config.GptConfig;
import com.softgallery.story_playground_server.config.StoryConfig;
import com.softgallery.story_playground_server.config.WebClientConfig;
import com.softgallery.story_playground_server.dto.content.ContentInsertDTO;
import com.softgallery.story_playground_server.dto.content.ContentOnlyDTO;
import com.softgallery.story_playground_server.dto.gpt.DalleInsertDTO;
import com.softgallery.story_playground_server.dto.gpt.GptRequestDTO;
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
import com.softgallery.story_playground_server.service.user.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class StoryService {
    private final PageRepository pageRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    public Long saveMessage(ContentInsertDTO contentInsertDTO) {
        Optional<PageEntity> safePage = pageRepository.findById(contentInsertDTO.getPageId());
        if(safePage.isEmpty()) throw new EntityNotFoundException();

        ContentEntity content = contentRepository.save(
                ContentEntity.builder()
                        .createdDate(LocalDateTime.now())
                        .content(contentInsertDTO.getContent())
                        .page(safePage.get())
                        .role(Role.user)
                        .build()
        );

        return content.getContentId();
    }

    public StoryIdDTO makeNewStory() {
        //String userEmail = WebClientConfig.getCurrentUserEmail();
        String userEmail = null;

        Optional<UserEntity> safeUser = userRepository.findByEmail(userEmail);
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

    public PageIdDTO makeNewPage(StoryIdDTO storyIdDTO) {
        //String userEmail = WebClientConfig.getCurrentUserEmail();
        String userEmail = null;
        Optional<UserEntity> safeUser = userRepository.findByEmail(userEmail);
        if(safeUser.isEmpty()) throw new EntityNotFoundException();

        Optional<StoryEntity> safeStory = storyRepository.findById(storyIdDTO.getStoryId());
        if(safeStory.isEmpty()) throw new EntityNotFoundException();

        Long currPageIndex=-1L;
        Optional<PageEntity> safeLargestPage = pageRepository.findFirstByStoryOrderByPageIndexDesc(safeStory.get());

        if(safeLargestPage.isEmpty()) currPageIndex = 1L;
        else currPageIndex = safeLargestPage.get().getPageIndex()+1;

        PageEntity pageEntity = pageRepository.save(
                PageEntity.builder()
                        .pageIndex(currPageIndex)
                        .story(safeStory.get())
                        .build()
        );

        return new PageIdDTO(pageEntity.getPageId());
    }

    public ContentOnlyDTO receiveMessage(Long storyId) {
        Optional<StoryEntity> safeStory = storyRepository.findById(storyId);
        if (safeStory.isEmpty()) throw new EntityNotFoundException();

        List<PageEntity> pages = pageRepository.findAllByStoryOrderByPageIndex(safeStory.get());
        List<ContentEntity> contentEntities = new ArrayList<>();

        Optional<PageEntity> safePage = pageRepository.findFirstByStoryOrderByPageIndexDesc(safeStory.get());
        if (safePage.isEmpty()) throw new EntityNotFoundException();

        for (PageEntity page : pages) {
            List<ContentEntity> contents = contentRepository.findAllByPageOrderByCreatedDate(page);
            contentEntities.addAll(contents);
        }

        GptRequestDTO gptRequestDTO = GptRequestDTO.builder()
                .model(GptConfig.DEFAULT_MODEL)
                .messages(
                        contentEntities.stream()
                                .map(content -> new GptRequestDTO.Message(content.getRole(), content.getContent()))
                                .collect(Collectors.toList())
                )
                .build();

        String llmResult;
        try {
            llmResult = webClient.post()
                    .uri(GptConfig.GPT_URI)
                    .header(GptConfig.AUTHORIZATION, GptConfig.Bearer + apiKey)
                    .bodyValue(gptRequestDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            llmResult = "Error: " + ex.getResponseBodyAsString();
        }

        ContentEntity content = ContentEntity.builder()
                .createdDate(LocalDateTime.now())
                .content(llmResult)
                .role(Role.assistant)
                .page(safePage.get())
                .build();

        ContentEntity savedContent = contentRepository.save(content);
        return new ContentOnlyDTO(savedContent.getContentId(), savedContent.getContent());
    }

    public String generateImage(Long storyId) {
        DalleInsertDTO dalleInsertDTO = new DalleInsertDTO(GptConfig.DALLE_PROMPT, 1L, GptConfig.DALLE_SIZE);

        try {
            return webClient.post()
                    .uri(GptConfig.DALLE_URI)
                    .header(GptConfig.AUTHORIZATION, GptConfig.Bearer + apiKey)
                    .bodyValue(dalleInsertDTO)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            return "Error: " + e.getResponseBodyAsString();
        }
    }
}
