package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long questionId);
    List<Answer> findByQuestionIdAndUserId(Long questionId, Long userId); // Modified to return a list
    List<Answer> findByQuestionIdOrderByCreatedAtDesc(Long questionId);
    List<Answer> findByQuestionIdAndUserIdOrderByCreatedAtDesc(Long questionId, Long userId);
}