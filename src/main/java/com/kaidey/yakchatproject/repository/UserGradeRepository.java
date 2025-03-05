package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.UserGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserGradeRepository extends JpaRepository<UserGrade, Long> {
    Optional<UserGrade> findByUserId(Long userId);
}
