package com.softgallery.story_playground_server.dto.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SttRequestDTO {
    private byte[] image;
}
