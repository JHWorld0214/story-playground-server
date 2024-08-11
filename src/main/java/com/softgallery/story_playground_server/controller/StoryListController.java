package com.softgallery.story_playground_server.controller;

import com.softgallery.story_playground_server.dto.story.StoryInfoDTO;
import com.softgallery.story_playground_server.service.story.StoryListService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class StoryListController {
    private final StoryListService storyListService;

    public StoryListController(final StoryListService storyListService) {
        this.storyListService = storyListService;
    }

    @GetMapping("/list/incomplete")
    public ResponseEntity<List<StoryInfoDTO>> loadIncompleteStories(@RequestHeader(name = "Authorization") String userToken) {
        List<StoryInfoDTO> incompleteStories = storyListService.findIncompleteStoriesMadeByUserName(userToken);

        return ResponseEntity.ok().body(incompleteStories);
    }

    @GetMapping("/list/complete")
    public ResponseEntity<List<StoryInfoDTO>> loadCompleteStories(@RequestHeader(name = "Authorization") String userToken) {
        List<StoryInfoDTO> completeStories = storyListService.findCompleteStoriesMadeByUserName(userToken);

        return ResponseEntity.ok().body(completeStories);
    }
}
