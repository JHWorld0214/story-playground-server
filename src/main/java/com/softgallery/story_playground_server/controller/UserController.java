package com.softgallery.story_playground_server.controller;

import com.softgallery.story_playground_server.dto.user.UserDTO;
import com.softgallery.story_playground_server.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register/user")
    public ResponseEntity<Boolean> registerNewUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok().body(userService.insertNewUser(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
        System.out.println("here");
        return ResponseEntity.ok().body("hello world");
    }
}
