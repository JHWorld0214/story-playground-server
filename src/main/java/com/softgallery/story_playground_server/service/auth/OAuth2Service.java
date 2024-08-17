package com.softgallery.story_playground_server.service.auth;

import com.softgallery.story_playground_server.dto.session.SessionIdDTO;
import com.softgallery.story_playground_server.entity.UserEntity;
import com.softgallery.story_playground_server.global.error.exception.UnauthorizedException;
import com.softgallery.story_playground_server.repository.UserRepository;
import com.softgallery.story_playground_server.service.user.Social;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
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

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.login.LoginException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    @Value("${jwt.expiration}")
    private long expireTimeMilliSecond;

    @Value("${jwt.secret}")
    private String secretKey;

    private static String staticSecretKey;

    @PostConstruct
    public void init() {
        staticSecretKey = secretKey;
    }

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

        Optional<UserEntity> safeUser = userRepository.findByEmail(email);
        if(safeUser.isEmpty()) throw new RuntimeException("No User or occur error during insert email " + email);

        return new SessionIdDTO(generateToken(safeUser.get().getUserId()));
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
        System.out.println("access token: " + accessToken);
        RestTemplate restTemplate = new RestTemplate();
        String infoUri = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        return restTemplate.exchange(infoUri, HttpMethod.GET, request, Map.class).getBody();
    }

    public String generateToken(final UUID id) {
        // 현재 시간과 만료 시간 설정
        final Date now = new Date();
        final Date expiredDate = new Date(now.getTime() + expireTimeMilliSecond);

        SecretKey noStringSecret = new SecretKeySpec(staticSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());

        // JWT Claims 설정 및 생성
        return Jwts.builder()
                .claim("userId", id.toString()) // 사용자 ID 클레임 추가
                .subject(id.toString())
                .issuedAt(now) // 발행 시간 설정
                .expiration(expiredDate) // 만료 시간 설정
                .signWith(noStringSecret) // 서명 키 설정 (Algorithm 자동 선택)
                .compact(); // 최종적으로 JWT 토큰을 생성
    }

    public static String getOnlyToken(String token) {
        return token.split(" ")[1];
    }

    public static Boolean isExpired(String token) {
        SecretKey noStringSecret = new SecretKeySpec(staticSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        return Jwts.parser().verifyWith(noStringSecret).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public static UUID extractMemberId(String token) {
        SecretKey noStringSecret = new SecretKeySpec(staticSecretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        String userIdString = Jwts.parser().verifyWith(noStringSecret).build().parseSignedClaims(token).getPayload().get("userId", String.class);
        return UUID.fromString(userIdString);
    }

}
