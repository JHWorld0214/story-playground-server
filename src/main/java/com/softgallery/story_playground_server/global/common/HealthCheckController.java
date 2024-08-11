package com.softgallery.story_playground_server.global.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @RequestMapping("/")
    public String WagekeeperServer() {
        return "Yage Server!";
    }

    @GetMapping("/example")
    public ResponseEntity<SuccessResponse<?>> example() {
        return SuccessResponse.ok("example");
    }
}
