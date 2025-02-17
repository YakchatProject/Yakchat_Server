package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;
import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {

    private Long id;
    private String content;
    private Boolean isAnonymous; // 익명 여부 추가
    private Long userId;
    private String userName;
    private Long questionId;
    private Map<String, String> images;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer likeCount;

    public AnswerDto(String content, Long questionId, Long userId) {
        this.content = content;
        this.questionId = questionId;
        this.userId = userId;
    }

//    작성 날짜: 답변 등록 시간 (LocalDateTime createdAt)
//    수정 날짜: 답변 수정 시간 (LocalDateTime updatedAt)

}
