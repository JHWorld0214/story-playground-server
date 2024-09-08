//package com.softgallery.story_playground_server.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//
//@Entity
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name="page")
//public class PageEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long pageId;
//
//    @Column(nullable = false)
//    private Long pageIndex;
//
//    @ManyToOne
//    @JoinColumn(name="story_id", nullable = false)
//    private StoryEntity story;
//
//    @OneToOne(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
//    private ImageEntity image;
//
//    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Column(nullable = false)
//    private List<ContentEntity> contents;
//}
