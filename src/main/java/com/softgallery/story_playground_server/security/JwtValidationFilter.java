package com.softgallery.story_playground_server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softgallery.story_playground_server.entity.UserEntity;
import com.softgallery.story_playground_server.global.error.dto.ErrorResponse;
import com.softgallery.story_playground_server.global.error.exception.UnauthorizedTokenException;
import com.softgallery.story_playground_server.service.auth.OAuth2Service;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getServletPath().equals("/code") ||
                request.getServletPath().startsWith("/swagger-ui") ||
                request.getServletPath().startsWith("/v3/api-docs") ||
                request.getServletPath().startsWith("/dontopen")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                setErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Token is null or invalid format", response);
                return;
            }

            String token = authorization.split(" ")[1];

            // 만료 여부와 형식 검증
            if (OAuth2Service.isExpired(token)) {
                setErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired", response);
                return;
            }

            // 유효하지 않은 토큰 형식이나 서명 오류 처리
            UUID userId = OAuth2Service.extractMemberId(token);

            UserEntity userEntity = UserEntity.builder().userId(userId).build();

        } catch (UnauthorizedTokenException | ExpiredJwtException e) {
            System.out.println("unauthorized or expired token");
            setErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired or invalid format", response);
            return;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            System.out.println("invalid jwt");
            setErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT token or signature error", response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private void setErrorResponse(int status, String message, HttpServletResponse response) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(status)
                .message(message)
                .build();

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
