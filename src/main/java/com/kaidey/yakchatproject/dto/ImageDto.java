package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDto {
    private byte[] data; // 이미지 데이터 (바이너리 형태)
    private String fileName; // 파일 이름
    // 필요한 경우 질문 ID나 답변 ID도 추가할 수 있습니다.
    private Long questionId; // (선택적) 관련 질문 ID
    private Long answerId; // (선택적) 관련 답변 ID
}
