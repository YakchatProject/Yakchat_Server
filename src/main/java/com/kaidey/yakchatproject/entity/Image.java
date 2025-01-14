package com.kaidey.yakchatproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url; // 이미지 URL

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 이미지 업로드한 사용자

    @ManyToOne
    @JoinColumn(name = "question_id") // 외래 키: 질문 ID
    private Question question; // 관련 질문

    @ManyToOne
    @JoinColumn(name = "answer_id") // 외래 키: 답변 ID
    private Answer answer; // 관련 답변
}
