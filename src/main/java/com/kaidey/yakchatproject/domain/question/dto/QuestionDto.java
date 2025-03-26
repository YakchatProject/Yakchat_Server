package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {
    private Long id;
    private String content;
    private Long userId;
    private String userName;
    private Long questionId;
    private Map<String, String> images; // ✅ 변경: List<ImageDto> → Map<String, String>
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer likeCount;
    private List<AnswerDto> subAnswers = new ArrayList<>();

    public AnswerDto(String content, Long questionId, Long userId) {
        this.content = content;
        this.questionId = questionId;
        this.userId = userId;
    }
}
