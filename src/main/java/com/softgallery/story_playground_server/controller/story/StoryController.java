package com.softgallery.story_playground_server.controller.story;

import com.softgallery.story_playground_server.dto.content.ContentInsertDTO;
import com.softgallery.story_playground_server.dto.story.StoryIdDTO;
import com.softgallery.story_playground_server.global.common.SuccessResponse;
import com.softgallery.story_playground_server.service.story.StoryService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/story")
public class StoryController {
    private final StoryService storyService;
    private final UUID userId = UUID.fromString("1ca4ecb3-112a-4122-b9f0-1c7d96cd7940");

    @PostMapping("/generation")
    public ResponseEntity<SuccessResponse<?>> makeNewStory() {
        return SuccessResponse.ok(storyService.makeNewStory(userId));
    }

    @PostMapping("/page/generation")
    public ResponseEntity<SuccessResponse<?>> makeNewPage(@RequestBody StoryIdDTO storyIdDTO) {
        return SuccessResponse.ok(storyService.makeNewPage(userId, storyIdDTO));
    }

    @PostMapping("/content/send")
    public ResponseEntity<SuccessResponse<?>> saveMessage(@RequestBody ContentInsertDTO contentInsertDTO) {
        Long contentId = storyService.saveMessage(contentInsertDTO);

        return SuccessResponse.ok(contentId);
    }

    @GetMapping("/content/receive/{storyId}")
    public ResponseEntity<SuccessResponse<?>> receiveMessage(@PathVariable("storyId") Long storyId) {
        return SuccessResponse.ok(storyService.receiveMessage(storyId));
    }
}
