package com.kaidey.yakchatproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.kaidey.yakchatproject.entity.enums.GradeType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeType grade = GradeType.GRAY; // 기본 등급

    private int questionCount = 0;
    private int acceptedCount = 0;
    private int likeCount = 0;
    private int purchasedMaterialCount = 0;
    private int soldMaterialCount = 0;
}
