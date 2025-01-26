package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDto {
    private Long id;
    private String username;
    private Integer age;
    private String school;
    private String grade;
//    private String ProfileImage;
//    private String ProfileImageUrl;
    private List<ImageDto> images;

}