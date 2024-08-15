package com.softgallery.story_playground_server.service.session;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class HeaderBasedSessionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 헤더에서 세션 ID를 추출
        String sessionId = request.getHeader("Authorization");

        if (sessionId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 세션에서 SecurityContext 가져오기
            HttpSession session = request.getSession(false);
            if (session != null && sessionId.equals(session.getId())) {
                SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

                if (securityContext != null) {
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication != null) {
                        // SecurityContextHolder에 인증 정보 설정
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
