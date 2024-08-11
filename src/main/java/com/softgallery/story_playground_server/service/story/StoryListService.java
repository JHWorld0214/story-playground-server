package com.softgallery.story_playground_server.service.story;

import com.softgallery.story_playground_server.service.auth.JWTUtil;
import com.softgallery.story_playground_server.dto.story.StoryInfoDTO;
import com.softgallery.story_playground_server.entity.StoryEntity;
import com.softgallery.story_playground_server.repository.StoryRepository;
import com.softgallery.story_playground_server.service.community.CommunityService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StoryListService {
    private final StoryRepository storyRepository;
    private final JWTUtil jwtUtil;

    public StoryListService(final StoryRepository storyRepository, JWTUtil jwtUtil) {
        this.storyRepository = storyRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<StoryInfoDTO> findIncompleteStoriesMadeByUserName(String userToken) {
        String username = jwtUtil.getUsername(JWTUtil.getOnlyToken(userToken));
        List<StoryEntity> stories = storyRepository.findAllByUsernameAndIsCompletedOrderByModifiedDateDesc(username, false);

        return CommunityService.entityListToInfo(stories);
    }

    public List<StoryInfoDTO> findCompleteStoriesMadeByUserName(String userToken) {
        String username = jwtUtil.getUsername(JWTUtil.getOnlyToken(userToken));
        List<StoryEntity> stories = storyRepository.findAllByUsernameAndIsCompletedOrderByModifiedDateDesc( username, true);

        return CommunityService.entityListToInfo(stories);
    }
}