package com.softgallery.story_playground_server.dto.user;

import com.softgallery.story_playground_server.service.user.Social;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInsertDTO {
    private String email;
    private String name;
    private String picture;
    private Social social;
}
