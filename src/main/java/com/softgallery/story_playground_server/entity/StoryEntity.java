package com.softgallery.story_playground_server.entity;

import com.softgallery.story_playground_server.service.story.Visibility;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="story")
public class StoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storyId;

    @Column
    @NotNull
    private String title;

    @Column
    @NotNull
    private String username;

    @Column
    @Nullable
    private String topic;

    @Column
    @NotNull
    private Long level;

    @Column
    private Boolean isCompleted;

    @Column(length = 10000)
    @NotNull
    private String content;

    @Column
    @NotNull
    private LocalDateTime modifiedDate;

    @Column
    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    @NotNull
    private Long likeNum;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    @NotNull
    private Long dislikeNum;


    public StoryEntity(Long storyId, String title, String username, String topic, Long level, Boolean isCompleted, String content, LocalDateTime modifiedDate, Visibility visibility, Long likeNum, Long dislikeNum) {
        this.storyId = storyId;
        this.title = title;
        this.username = username;
        this.topic = topic;
        this.level = level;
        this.isCompleted = isCompleted;
        this.content = content;
        this.modifiedDate = modifiedDate;
        this.visibility = visibility;
        this.likeNum = likeNum;
        this.dislikeNum = dislikeNum;
    }
}
