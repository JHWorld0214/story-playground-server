package com.softgallery.story_playground_server.service.auth;

import com.softgallery.story_playground_server.dto.session.SessionIdDTO;
import com.softgallery.story_playground_server.entity.UserEntity;
import com.softgallery.story_playground_server.global.error.exception.UnauthorizedException;
import com.softgallery.story_playground_server.repository.UserRepository;
import com.softgallery.story_playground_server.service.user.Social;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2Service {
    private final WebClient webClient;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public SessionIdDTO authenticateUserWithGoogle(String authorizationCode, HttpServletRequest request) {
        System.out.println("runned");
        String accessToken = getAccessToken(authorizationCode);
        System.out.println("access token out : " + accessToken);

        Map<String, Object> userInfo = getUserInfo(accessToken);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        System.out.println("user email" + email);
        System.out.println("username" + name);

        if (!userRepository.existsByEmail(email)) {
            userRepository.save(UserEntity.builder()
                    .name(name)
                    .email(email)
                    .social(Social.google)
                    .picture(picture)
                    .build());
        }

        OAuth2User oAuth2User = new DefaultOAuth2User(
                null,
                userInfo,
                "email"
        );

        Authentication authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                null,
                "google"
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("user", oAuth2User);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        String sessionId = session.getId();

        return new SessionIdDTO(sessionId);
    }

    private String getAccessToken(String authorizationCode) {
        String decodedCode;
        try {
            decodedCode = URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // UTF-8이 지원되지 않는 경우는 사실상 없으므로 런타임 예외로 처리
            throw new RuntimeException("UTF-8 encoding is not supported", e);
        }

        WebClient webClient = WebClient.builder()
                .baseUrl("https://oauth2.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", decodedCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        Map<String, Object> response = webClient.post()
                .uri("/token")
                .bodyValue(params)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("access_token");
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String infoUri = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        return restTemplate.exchange(infoUri, HttpMethod.GET, request, Map.class).getBody();
    }
}
