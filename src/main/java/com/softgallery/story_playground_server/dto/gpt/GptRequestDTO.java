package com.softgallery.story_playground_server.dto.gpt;

import com.softgallery.story_playground_server.service.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GptRequestDTO {
    private String model;
    private List<Message> messages;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private Role role;
        private String content;
    }
}
