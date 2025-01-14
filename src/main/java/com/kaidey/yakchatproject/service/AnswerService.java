package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.AnswerDto;
import com.kaidey.yakchatproject.entity.Answer;
import com.kaidey.yakchatproject.entity.Question;
import com.kaidey.yakchatproject.repository.AnswerRepository;
import com.kaidey.yakchatproject.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository; // QuestionRepository 추가

    // 답변 생성
    public Answer createAnswer(AnswerDto answerDto) {
        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found")); // 질문 객체 조회

        Answer answer = new Answer();
        answer.setContent(answerDto.getContent());
        answer.setQuestion(question); // 질문 객체 설정
        return answerRepository.save(answer);
    }

    // 특정 답변 조회
    public Answer getAnswerById(Long id) {
        return answerRepository.findById(id).orElseThrow(() -> new RuntimeException("Answer not found"));
    }

    // 모든 답변 조회
    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    // 답변 업데이트
    public Answer updateAnswer(Long id, AnswerDto answerDto) {
        Answer answer = getAnswerById(id);
        answer.setContent(answerDto.getContent());
        return answerRepository.save(answer);
    }

    // 답변 삭제
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }
}
