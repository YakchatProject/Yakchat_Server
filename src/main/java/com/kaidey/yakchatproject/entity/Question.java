package com.kaidey.yakchatproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // 질문 제목

    @Lob
    private String content; // 질문 내용

    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간
    private LocalDateTime updatedAt = LocalDateTime.now(); // 수정 시간
    private Integer likeCount;// 좋아요 수

    @ManyToOne
    private User createdBy; // 질문 작성자


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // 질문 카테고리

    @Column(nullable = false)
    private Boolean isAnonymous; // 익명 여부

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images; // 질문에 포함된 이미지 리스트





}
