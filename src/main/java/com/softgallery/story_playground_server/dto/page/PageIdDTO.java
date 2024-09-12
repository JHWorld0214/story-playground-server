package com.softgallery.story_playground_server.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageIdDTO {
    @JsonProperty("page_id")
    private Long pageId;
}
