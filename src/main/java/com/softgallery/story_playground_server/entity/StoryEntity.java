package com.softgallery.story_playground_server.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name="story")
public class StoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storyId;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean isCompleted;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<PageEntity> pages;

    public void changeModifiedDate() {
        this.modifiedDate = LocalDateTime.now();
    }
}

