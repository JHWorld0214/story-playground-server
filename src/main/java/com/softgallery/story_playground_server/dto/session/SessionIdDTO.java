package com.softgallery.story_playground_server.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SessionIdDTO {
    @JsonProperty("session_id")
    private String sessionId;
}
