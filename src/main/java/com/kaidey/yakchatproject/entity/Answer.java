package com.kaidey.yakchatproject.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // 관련 질문

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 답변 작성자

    @Column(nullable = false, length = 1000)
    private String content; // 답변 내용

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images; // 답변에 포함된 이미지 리스트

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Integer likeCount;
}