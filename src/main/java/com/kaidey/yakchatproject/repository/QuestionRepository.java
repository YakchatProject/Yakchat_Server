package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubjectId(Long subjectId); // Find questions by subject ID
    List<Question> findByTitleContainingOrContentContaining(String title, String content);

}