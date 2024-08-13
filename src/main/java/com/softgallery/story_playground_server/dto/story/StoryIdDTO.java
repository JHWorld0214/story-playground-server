package com.softgallery.story_playground_server.dto.story;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoryIdDTO {
    @JsonProperty("story_id")
    private Long storyId;
}
