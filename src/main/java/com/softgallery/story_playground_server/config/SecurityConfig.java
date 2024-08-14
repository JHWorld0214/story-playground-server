package com.softgallery.story_playground_server.config;

import com.softgallery.story_playground_server.auth.CustomAuthenticationSuccessHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
@Getter
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/", "/example", "dontopen/**", "/dontopen/visitor/submit", // 테스트 API들
                                "/swagger-ui/**", "/v3/api-docs/**"        // 서비스 및 Swagger API들
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .successHandler(successHandler) // 인증 성공 핸들러 설정
                );

        return http.build();
    }
}
