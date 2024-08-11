package com.softgallery.story_playground_server.service.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Choice implements Serializable {

    private Message message;
    private Integer index;
    @JsonProperty("finish_reason")
    private String finishReason;

    @Builder
    public Choice(Message message, Integer index, String finishReason) {
        this.message = message;
        this.index = index;
        this.finishReason = finishReason;
    }
}
