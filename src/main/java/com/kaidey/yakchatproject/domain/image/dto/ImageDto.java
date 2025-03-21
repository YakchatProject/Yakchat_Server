package com.kaidey.yakchatproject.domain.image.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private String fileName; // 파일 이름
    private Long id;
    private String base64Data;
    private Long questionId; // (선택적) 관련 질문 ID
    private Long answerId; // (선택적) 관련 답변 ID
    private Long userId;
    private String url; // 이미지 URL
    private String mime; // MIME 타입
}
