package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private String fileName; // 파일 이름
    private Long questionId; // (선택적) 관련 질문 ID
    private Long answerId; // (선택적) 관련 답변 ID
    private String url; // 이미지 URL
}
