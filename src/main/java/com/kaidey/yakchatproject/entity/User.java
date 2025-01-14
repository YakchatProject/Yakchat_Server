package com.kaidey.yakchatproject.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String school;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Boolean isActive; // 회원 상태: 활성화 여부

    @Column(nullable = false)
    private LocalDateTime createdAt; // 등록 날짜

    private LocalDateTime lastLoginAt; // 마지막 로그인

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

}