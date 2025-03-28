package com.kaidey.yakchatproject.domain.question.dto;

import com.kaidey.yakchatproject.domain.image.dto.ImageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDto {
    private Long id;
    private String title;
    private String content;
    private Long subjectId;
    private String subjectName;
    private Long userId;
    private String userName;
    private List<ImageDto> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private Integer viewCount;
}
