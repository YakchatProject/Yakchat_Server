package com.kaidey.yakchatproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] data; // 이미지 데이터

    private String fileName; // 파일 이름

    @ManyToOne // 다대일 관계
    @JoinColumn(name = "question_id", nullable = true)
    private Question question; // 연관된 질문 (nullable)

    @ManyToOne // 다대일 관계
    @JoinColumn(name = "answer_id", nullable = true)
    private Answer answer; // 연관된 답변 (nullable)
}
