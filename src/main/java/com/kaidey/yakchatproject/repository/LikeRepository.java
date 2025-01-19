package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("SELECT l FROM Like l LEFT JOIN FETCH l.user u LEFT JOIN FETCH l.question q WHERE u.id = :userId AND q.id = :questionId")
    List<Like> findByUserIdAndQuestionId(Long userId, Long questionId);

    @Query("SELECT l FROM Like l LEFT JOIN FETCH l.user u LEFT JOIN FETCH l.answer a WHERE u.id = :userId AND a.id = :answerId")
    Optional<Like> findByUserIdAndAnswerId(Long userId, Long answerId);

    long countByQuestionId(Long questionId);

    long countByAnswerId(Long answerId);
}