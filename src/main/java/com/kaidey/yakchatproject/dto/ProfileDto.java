package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileDto {
    private String username;
    private Integer age;
    private String school;
    private String grade;
    private String ProfileImage;
    private String ProfileImageUrl;

}