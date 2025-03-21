package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class QuestionWithAnswersDto {
    private Long id;
    private String title;
    private String content;
    private boolean isQuestionOwner;
    private Long subjectId;
    private String subjectName;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int viewCount;
    private List<ImageDto> images;

    // 추가: 관련된 답변 목록
    private List<AnswerDto> answers;
}
