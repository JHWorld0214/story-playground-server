//package com.softgallery.story_playground_server.auth;
//
//import com.softgallery.story_playground_server.entity.UserEntity;
//import com.softgallery.story_playground_server.repository.UserRepository;
//import com.softgallery.story_playground_server.service.user.Social;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//    private final UserRepository userRepository;
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//
//        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
//        String social = oauthToken.getAuthorizedClientRegistrationId();
//
//        System.out.println(social);
//
//        String email = oAuth2User.getAttribute("email");
//        String name = oAuth2User.getAttribute("name");
//        String picture = oAuth2User.getAttribute("picture");
//
//        System.out.println(picture);
//
//        Social socialName = Social.naver;
//        if(social.equals("google")) socialName = Social.google;
//
//        if(!userRepository.existsByEmail(email)) {
//            userRepository.save(
//                    UserEntity.builder()
//                            .name(name)
//                            .email(email)
//                            .social(socialName)
//                            .picture(picture)
//                            .build()
//            );
//        }
//
//        response.setContentType("application/json");
//        response.setStatus(HttpServletResponse.SC_OK);
//
//        // JSON 응답 생성
//        String jsonResponse = "{\"code\":\"200\",\"status\":\"success\",\"message\":\"Authentication successful\"}";
//        response.getWriter().write(jsonResponse);
//    }
//}