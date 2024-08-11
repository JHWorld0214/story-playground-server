package com.softgallery.story_playground_server.dto.moderation;

import lombok.Getter;

@Getter
public class WordFilterDTO {
    boolean bad;
    String reason;

    public WordFilterDTO(boolean flag, String reason) {
        this.bad = flag;
        this.reason = reason;
    }
}
