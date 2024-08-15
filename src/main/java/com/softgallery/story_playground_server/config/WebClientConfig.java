package com.softgallery.story_playground_server.config;

import com.softgallery.story_playground_server.global.error.exception.EntityNotFoundException;
import com.softgallery.story_playground_server.service.user.UserService;
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


    public static String getCurrentUserEmail() {
        OAuth2User oAuth2User = getOAuth2User();

        if(oAuth2User==null) throw new EntityNotFoundException();

        return oAuth2User.getAttribute("email");
    }
}
