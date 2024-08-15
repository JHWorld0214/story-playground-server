package com.softgallery.story_playground_server.controller.auth;

import com.softgallery.story_playground_server.dto.session.SessionIdDTO;
import com.softgallery.story_playground_server.global.common.SuccessResponse;
import com.softgallery.story_playground_server.service.auth.OAuth2Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class OAuth2Controller {
    private final OAuth2Service authService;

    @PostMapping("/code")
    public ResponseEntity<SuccessResponse<?>> handleGoogleLogin(@RequestBody Map<String, String> request) {
        String authorizationCode = request.get("code");
        SessionIdDTO sessionIdDTO = authService.authenticateUserWithGoogle(authorizationCode);
        return SuccessResponse.ok(sessionIdDTO);
    }
}
