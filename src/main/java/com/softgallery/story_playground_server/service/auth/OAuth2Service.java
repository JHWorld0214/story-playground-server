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

    private final Map<String, Map<String, Object>> sessions = new HashMap<>();

    public SessionIdDTO authenticateUserWithGoogle(String authorizationCode) {
        // 1. Authorization Code를 사용해 액세스 토큰을 요청
        String accessToken = getAccessToken(authorizationCode);

        // 2. 액세스 토큰을 사용해 사용자 정보 요청
        Map<String, Object> userInfo = getUserInfo(accessToken);

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        // 3. 사용자를 DB에 저장 (이미 존재하지 않는 경우)
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(UserEntity.builder()
                    .name(name)
                    .email(email)
                    .social(Social.google)
                    .picture(picture)
                    .build());
        }

        // 4. OAuth2User 객체 생성 및 세션에 저장
        OAuth2User oAuth2User = new DefaultOAuth2User(
                null, // 권한 (추가 가능)
                userInfo, // 사용자 정보
                "email" // 키 (사용자 식별을 위한 속성)
        );

        Authentication authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                null, // 권한
                "google"
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, userInfo);

//        // 5. 세션 생성 및 세션 ID 출력
//        HttpSession session = request.getSession(); // 세션 생성 또는 기존 세션 반환
//        session.setAttribute("user", oAuth2User); // 세션에 사용자 정보 저장
//        String sessionId = session.getId(); // 세션 ID 가져오기

        return new SessionIdDTO(sessionId);
    }

    public String getSessionUserEmail(String sessionId) {
        Map<String, Object> userInfo = sessions.get(sessionId);

        if(userInfo==null) throw new UnauthorizedException();

        return (String) userInfo.get("email");
    }

    private String getAccessToken(String authorizationCode) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUri = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        Map<String, Object> tokenResponse = restTemplate.postForObject(tokenUri, request, Map.class);

        return (String) tokenResponse.get("access_token");

//        // Google OAuth 서버에서 액세스 토큰을 가져옵니다.
//        Map<String, String> response = this.webClient.post()
//                .uri("https://oauth2.googleapis.com/token")
//                .bodyValue(Map.of(
//                        "client_id", clientId,
//                        "client_secret", clientSecret,
//                        "code", authorizationCode,
//                        "grant_type", "authorization_code",
//                        "redirect_uri", redirectUri
//                ))
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError(), clientResponse ->
//                        clientResponse.bodyToMono(String.class)
//                                .map(error -> new RuntimeException("4xx error: " + error))
//                )
//                .onStatus(status -> status.is5xxServerError(), clientResponse ->
//                        clientResponse.bodyToMono(String.class)
//                                .map(error -> new RuntimeException("5xx error: " + error))
//                )
//                .bodyToMono(Map.class)
//                .block();
//
//        return response.get("access_token");
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        // 액세스 토큰을 사용해 Google 사용자 정보를 요청합니다.
        return this.webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}
