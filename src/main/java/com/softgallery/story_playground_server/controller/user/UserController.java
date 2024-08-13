package com.softgallery.story_playground_server.controller.user;

import com.softgallery.story_playground_server.dto.user.UserDTO;
import com.softgallery.story_playground_server.global.common.SuccessResponse;
import com.softgallery.story_playground_server.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<SuccessResponse<?>> signIn(@RequestBody UserInsertDTO userInsertDTO) {
        UserIdResponseDTO userIdResponseDTO = userService.signIn(userInsertDTO);
        return SuccessResponse.ok(userIdResponseDTO);
    }
}
