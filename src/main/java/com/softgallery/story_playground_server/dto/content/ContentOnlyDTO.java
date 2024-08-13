package com.softgallery.story_playground_server.dto.content;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentOnlyDTO {
    @JsonProperty("content_id")
    private Long contentId;

    private String content;
}
