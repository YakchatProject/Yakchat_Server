package com.kaidey.yakchatproject.domain.answer.repository;

import com.kaidey.yakchatproject.domain.answer.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionIdOrderByCreatedAtDesc(Long questionId);
    List<Answer> findByQuestionIdAndUserIdOrderByCreatedAtDesc(Long questionId, Long userId);
    boolean existsByQuestionIdAndIsAcceptedTrue(Long questionId);
    List<Answer> findByQuestionIdOrderByIsAcceptedDescCreatedAtAsc(Long questionId);

}