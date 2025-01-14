package com.kaidey.yakchatproject.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDto {

    private Long id;
    private String title;
    private String content;
    private Boolean isAnonymous;
    private String subject;
    private Long userId;
    private List<String> imagePaths;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;

    // 작성 날짜: 질문 등록 시간 (LocalDateTime createdAt)
    //수정 날짜: 질문 수정 시간 (LocalDateTime updatedAt)
    //좋아요 수: 질문 좋아요 카운트 (Integer likeCount)
}