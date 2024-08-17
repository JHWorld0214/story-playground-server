package com.softgallery.story_playground_server.config;

import com.softgallery.story_playground_server.global.error.exception.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(GptConfig.BASE_URL)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public static OAuth2User getOAuth2User(HttpServletRequest request) {
        // 세션 ID를 출력
        String sessionId = getSessionIdFromCookies(request);
        System.out.println("Session ID: " + sessionId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            System.out.println("Authentication is null");
        } else {
            System.out.println("Authentication principal: " + authentication.getPrincipal());
        }

        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            return (OAuth2User) authentication.getPrincipal();
        }
        return null;
    }

    public static String getCurrentUserEmail(HttpServletRequest request) {
        OAuth2User oAuth2User = getOAuth2User(request);

        if (oAuth2User == null) throw new EntityNotFoundException();

        return oAuth2User.getAttribute("email");
    }

    private static String getSessionIdFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
