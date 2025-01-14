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
    private String username;
    private String school;
    private Integer grade;
    private Integer age;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

//회원 상태: 활성화 여부 (Boolean isActive)
//등록 날짜: 계정 생성 날짜 (LocalDateTime createdAt)
//마지막 로그인: 최근 로그인 시간 (LocalDateTime lastLoginAt)
}