package com.softgallery.story_playground_server.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GPTResponseDTO {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private Message message;
        private String finish_reason;
        private int index;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}

