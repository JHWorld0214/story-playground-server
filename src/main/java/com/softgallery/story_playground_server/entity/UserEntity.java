package com.softgallery.story_playground_server.entity;

import com.softgallery.story_playground_server.service.Social;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Table(name="user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String picture;

    @Column(length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Social social;
}
