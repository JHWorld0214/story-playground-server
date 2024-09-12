package com.softgallery.story_playground_server.dto.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentOnlyDTO {
    @JsonProperty("content_id")
    private Long contentId;

    private String content;
}
