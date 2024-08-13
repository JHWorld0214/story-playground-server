package com.softgallery.story_playground_server.dto.content;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContentInsertDTO {

    private String content;

    @JsonProperty("story_id")
    private Long storyId;

    @JsonProperty("page_id")
    private Long pageId;
}
