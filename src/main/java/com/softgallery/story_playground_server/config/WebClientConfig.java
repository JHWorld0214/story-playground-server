package com.softgallery.story_playground_server.config;

import com.softgallery.story_playground_server.global.error.exception.EntityNotFoundException;
import com.softgallery.story_playground_server.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
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

    public static OAuth2User getOAuth2User() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            return (OAuth2User) authentication.getPrincipal();
        }
        return null;
    }

    public static String getCurrentUserEmail(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new EntityNotFoundException();
        }

        OAuth2User oAuth2User = (OAuth2User) session.getAttribute("user"); // 세션에 저장된 사용자 정보 가져오기

        if (oAuth2User == null) {
            throw new EntityNotFoundException();
        }

        return oAuth2User.getAttribute("email"); // 이메일 정보 반환
    }
}

