package com.softgallery.story_playground_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="content")
public class ContentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(length = 1000, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name="page_id", nullable = false)
    private PageEntity page;
}
