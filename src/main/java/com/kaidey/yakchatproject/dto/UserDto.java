package com.kaidey.yakchatproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username; // 아이디
    private String password; // 비밀번호 (암호화된 형태로 저장될 것)
    private String school;
    private Integer grade;
    private Integer age;
    private Boolean isActive;
    private LocalDateTime createdAt; // 등록 날짜
    private LocalDateTime lastLoginAt; // 마지막 로그인
}
