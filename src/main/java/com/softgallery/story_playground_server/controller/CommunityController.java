package com.softgallery.story_playground_server.controller;

import com.softgallery.story_playground_server.dto.story.*;
import com.softgallery.story_playground_server.service.community.CommunityService;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/community")
public class CommunityController {
    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping("/ranking")
    public StoryNumberDTO getRanker(@RequestHeader("Authorization") String token) {
        return communityService.getRanker(token);
    }

    //topic: all, DB에 있는 topic, type: recent, like
    @GetMapping("/stories/{topic}/{type}")
    public StoryNumberDTO getBy(@PathVariable("topic") String topic, @PathVariable("type") String type,
                                @RequestHeader("Authorization") String token) {
        return communityService.getBy(topic, type, token);
    }

    @GetMapping("/topics")
    public Set<String> getAllTopics() {
        return communityService.getAllTopics();
    }

    //1: like / 0: unlike
    @GetMapping("/evaluation/{storyId}/{likeOrUnlike}")
    public StoryInfoDTO addEvaluation(@PathVariable("storyId") Long storyID, @PathVariable("likeOrUnlike") Long likeOrUnlike,
                                      @RequestHeader("Authorization") String token) {
        return communityService.addEvaluation(likeOrUnlike, storyID, token);
    }
}
