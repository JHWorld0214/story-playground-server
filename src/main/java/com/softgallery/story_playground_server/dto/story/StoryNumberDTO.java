package com.softgallery.story_playground_server.dto.story;

import java.util.List;
import lombok.Getter;

@Getter
public class StoryNumberDTO {
    private Long storyNum;
    private List<StoryInfoDTO> data;

    public StoryNumberDTO(){}

    public StoryNumberDTO(Long storyNum, List<StoryInfoDTO> data) {
        this.storyNum = storyNum;
        this.data = data;
    }
}
