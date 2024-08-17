package com.softgallery.story_playground_server.service.session;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtValidationFilter extends OncePerRequestFilter {

    private final Environment environment;

    public JwtValidationFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.replaceFirst("Bearer ", "");
        }

        if (token != null) {
            try {
                // 프로퍼티 파일에서 비밀 키 가져오기
                String secretKey = environment.getProperty("jwt.secret");

                // JWT 토큰 검증
                Jwts.parser()
                        .setSigningKey(secretKey.getBytes())
                        .parseClaimsJws(token); // 유효하지 않다면 예외 발생

                // 토큰이 유효하다면 필터 체인 진행
                filterChain.doFilter(request, response);

            } catch (ExpiredJwtException e) {
                // 토큰 만료 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired.");
            } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
                // JWT 형식 오류 또는 서명 오류 예외 처리
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid JWT token.");
            }
        } else {
            // 토큰이 없으면 기본 요청 처리
            filterChain.doFilter(request, response);
        }
    }
}
