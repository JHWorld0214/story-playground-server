package com.softgallery.story_playground_server.controller;

import com.softgallery.story_playground_server.dto.user.UserDTO;
import com.softgallery.story_playground_server.dto.moderation.WordFilterDTO;
import com.softgallery.story_playground_server.service.moderation.WordFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class TestController {
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok().body("auth OK");
    }

    @PostMapping("/test/moderation")
    public WordFilterDTO testPost(@RequestBody UserDTO userDTO) {
        return WordFilter.doFilterWithGptModeration(userDTO.getUsername());
    }
}
