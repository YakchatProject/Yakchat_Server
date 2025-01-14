package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubjectDto {
    private Long id;
    private String name; // 과목명
    private List<Long> questionIds; // 연결된 질문 ID 목록
    private List<Long> imageIds; // 연결된 이미지 ID 목록
}
