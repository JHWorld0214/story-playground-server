package com.softgallery.story_playground_server.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softgallery.story_playground_server.service.user.Social;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    @JsonProperty("user_id")
    private UUID userId;

    private String email;

    private String picture;

    private String name;

    private Social social;
}
