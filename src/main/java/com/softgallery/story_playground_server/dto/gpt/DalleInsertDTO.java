package com.softgallery.story_playground_server.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DalleInsertDTO {
    String prompt;
    Long n;
    String size;
}
