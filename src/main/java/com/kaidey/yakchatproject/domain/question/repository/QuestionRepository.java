package com.kaidey.yakchatproject.domain.question.repository;

import com.kaidey.yakchatproject.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTitleContainingOrContentContaining(String title, String content);
    List<Question> findByOrderByCreatedAtDesc();
    List<Question> findBySubjectIdOrderByCreatedAtDesc(Long subjectId);
    List<Question> findByOrderByCreatedAtAsc();
    List<Question> findBySubjectIdOrderByCreatedAtAsc(Long subjectId);
    List<Question> findTop5ByOrderByCreatedAtDesc();
    List<Question> findTop5BySubjectIdOrderByCreatedAtDesc(Long subjectId);


}