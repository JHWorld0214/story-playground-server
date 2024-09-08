package com.softgallery.story_playground_server.controller.story;

import com.softgallery.story_playground_server.dto.content.ContentInsertDTO;
import com.softgallery.story_playground_server.dto.story.StoryIdDTO;
import com.softgallery.story_playground_server.global.common.SuccessResponse;
import com.softgallery.story_playground_server.service.story.StoryService;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/generation")
    public ResponseEntity<SuccessResponse<?>> makeNewStory(@RequestHeader("Authorization") String token) {
        return SuccessResponse.ok(storyService.makeNewStory(token));
    }

//    @PostMapping("/page/generation")
//    public ResponseEntity<SuccessResponse<?>> makeNewPage(@RequestHeader("Authorization") String token, @RequestBody StoryIdDTO storyIdDTO) {
//        return SuccessResponse.ok(storyService.makeNewPage(token, storyIdDTO));
//    }

    @PostMapping("/content/send")
    public ResponseEntity<SuccessResponse<?>> saveMessage(@RequestHeader("Authorization") String token, @RequestBody ContentInsertDTO contentInsertDTO) {
        Long storyId = storyService.saveMessage(token, contentInsertDTO);

        return SuccessResponse.ok(storyService.receiveMessage(storyId));
    }

//    @GetMapping("/content/receive/{storyId}")
//    public ResponseEntity<SuccessResponse<?>> receiveMessage(@PathVariable("storyId") Long storyId) {
//        return SuccessResponse.ok(storyService.receiveMessage(storyId));
//    }

    @GetMapping("/content/receive/image/{storyId}")
    public ResponseEntity<SuccessResponse<?>> receiveImage(@PathVariable("storyId") Long storyId) {
        return SuccessResponse.ok(storyService.generateImage(storyId));
    }
}
