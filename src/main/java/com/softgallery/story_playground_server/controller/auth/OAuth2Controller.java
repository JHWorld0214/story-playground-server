package com.softgallery.story_playground_server.controller.auth;

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
    public ResponseEntity<String> handleGoogleLogin(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String authorizationCode = request.get("code");
        System.out.println("code : " + authorizationCode);
        authService.authenticateUserWithGoogle(authorizationCode, httpRequest);
        return ResponseEntity.ok("User authenticated successfully");
    }
}
