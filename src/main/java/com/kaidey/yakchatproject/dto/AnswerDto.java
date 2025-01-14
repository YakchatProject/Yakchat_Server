package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {

    private Long id;
    private String content;
    private Long userId;
    private Long questionId;
    private List<String> imagePaths;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;

//    작성 날짜: 답변 등록 시간 (LocalDateTime createdAt)
//    수정 날짜: 답변 수정 시간 (LocalDateTime updatedAt)

}
