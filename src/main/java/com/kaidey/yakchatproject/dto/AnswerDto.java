package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {

    private Long id;
    private String content;
    private Boolean isAnonymous; // 익명 여부
    private Long userId;
    private String userName;
    private Long questionId;
    private List<ImageDto> images;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer likeCount;

    private Integer stackOrder; // 스택 순서 추가
    private Long parentAnswerId; // 부모 답변 ID 추가
    private List<AnswerDto> subAnswers = new ArrayList<>();

    public AnswerDto(String content, Long questionId, Long userId, Integer stackOrder, Long parentAnswerId) {
        this.content = content;
        this.questionId = questionId;
        this.userId = userId;
        this.stackOrder = stackOrder;
        this.parentAnswerId = parentAnswerId;
    }
}
