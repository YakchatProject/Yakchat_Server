package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class QuestionLikeStatusDto {
    private long likes;
    private boolean isLiked;

    public QuestionLikeStatusDto(long likes, boolean isLiked) {
        this.likes = likes;
        this.isLiked = isLiked;
    }
}
