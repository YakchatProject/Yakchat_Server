package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.AnswerDto;
import com.kaidey.yakchatproject.entity.Answer;
import com.kaidey.yakchatproject.entity.Question;
import com.kaidey.yakchatproject.entity.Image;
import com.kaidey.yakchatproject.dto.ImageDto;
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
        try {
            Answer answer = new Answer();
            answer.setContent(answerDto.getContent());

            // 질문 설정
            answer.setQuestion(questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found")));

            // 이미지 설정
            if (answerDto.getImages() != null) {
                for (ImageDto imageDto : answerDto.getImages()) {
                    Image image = new Image();
                    image.setData(imageDto.getData());
                    image.setFileName(imageDto.getFileName());
                    image.setAnswer(answer); // 답변과 연관
                    answer.getImages().add(image);
                }
            }

            return answerRepository.save(answer); // 답변 저장
        } catch (Exception e) {
            throw new RuntimeException("Error creating answer: " + e.getMessage());
        }
    }

    // 특정 답변 조회
    public Answer getAnswerById(Long id) {
        try {
            return answerRepository.findById(id).orElseThrow(() -> new RuntimeException("Answer not found"));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving answer: " + e.getMessage());
        }
    }

    // 모든 답변 조회
    public List<Answer> getAllAnswers() {
        try {
            return answerRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all answers: " + e.getMessage());
        }
    }

    // 답변 업데이트
    public Answer updateAnswer(Long id, AnswerDto answerDto) {
        try {
            Answer answer = getAnswerById(id);
            answer.setContent(answerDto.getContent());
            return answerRepository.save(answer);
        } catch (Exception e) {
            throw new RuntimeException("Error updating answer: " + e.getMessage());
        }
    }

    // 답변 삭제
    public void deleteAnswer(Long id) {
        try {
            answerRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting answer: " + e.getMessage());
        }
    }
}