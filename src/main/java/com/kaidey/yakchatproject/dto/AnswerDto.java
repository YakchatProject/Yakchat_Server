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
    private String content; //내용
    private boolean isAccepted; //채탣
    private Boolean isAnonymous; // 익명 여부 추가
    private Long userId;
    private String userName;
    private Long questionId;
    private Map<String, String> images;
    private LocalDateTime createdAt; //생성 날짜
    private LocalDateTime modifiedAt; //수정 날짜
    private Integer likeCount;

    public AnswerDto(String content, Long questionId, Long userId, boolean isAccepted) {
        this.content = content;
        this.questionId = questionId;
        this.userId = userId;
        this.isAccepted = isAccepted;
    }

//    작성 날짜: 답변 등록 시간 (LocalDateTime createdAt)
//    수정 날짜: 답변 수정 시간 (LocalDateTime updatedAt)

}
