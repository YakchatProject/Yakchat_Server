package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubjectId(Long subjectId); // 과목 ID로 질문 조회
}
