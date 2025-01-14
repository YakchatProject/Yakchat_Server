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

    @Column(nullable = false)
    private String content; // 답변 내용

    @ManyToOne // 다대일 관계
    @JoinColumn(name = "question_id", nullable = false)
    private Question question; // 연관된 질문

    @ManyToOne // 작성자와의 관계
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 답변 작성자

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 등록 날짜

    private LocalDateTime modifiedAt; // 수정일

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>(); // 답변에 관련된 이미지들

    private int likes = 0; // 좋아요 수

    // 수정일 업데이트 메서드
    public void updateModifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }
}
