package com.kaidey.yakchatproject.entity;

import com.kaidey.yakchatproject.entity.enums.RoleType;
import jakarta.persistence.*;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

    // Getters and Setters
}