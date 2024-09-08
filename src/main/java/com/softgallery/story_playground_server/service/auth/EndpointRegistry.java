package com.softgallery.story_playground_server.service.auth;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EndpointRegistry {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private Set<String> endpoints = new HashSet<>();

    public EndpointRegistry(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    @PostConstruct
    public void init() {
        endpoints = requestMappingHandlerMapping.getHandlerMethods().keySet().stream()
                .map(info -> info.getPatternsCondition() != null ? info.getPatternsCondition().getPatterns() : new HashSet<>()) // Null-safe 처리
                .flatMap(Set::stream)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public boolean isEndpointExists(String path) {
        return endpoints.contains(path);
    }
}
