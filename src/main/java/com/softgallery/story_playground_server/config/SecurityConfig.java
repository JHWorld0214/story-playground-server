package com.softgallery.story_playground_server.config;

//import com.softgallery.story_playground_server.auth.CustomAuthenticationSuccessHandler;
import com.softgallery.story_playground_server.service.session.HeaderBasedSessionFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
@Getter
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/", "/example", "dontopen/**", "/dontopen/visitor/submit", // 테스트 API들
                                "/swagger-ui/**", "/v3/api-docs/**",        // 서비스 및 Swagger API들
                                "/code", "/api/user/info"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                )
                .addFilterBefore(new HeaderBasedSessionFilter(), SecurityContextHolderFilter.class); // 커스텀 필터 등록


        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 경로에 대해 CORS 설정
                        .allowedOrigins("http://localhost:3000") // 허용할 출처 설정
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메소드 설정
                        .allowedHeaders("*") // 허용할 헤더 설정
                        .allowCredentials(true); // 쿠키와 함께 요청 허용
            }
        };
    }
}
