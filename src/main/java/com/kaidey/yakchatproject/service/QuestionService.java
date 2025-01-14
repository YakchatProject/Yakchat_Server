package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.QuestionDto;
import com.kaidey.yakchatproject.entity.Question;
import com.kaidey.yakchatproject.entity.Subject;
import com.kaidey.yakchatproject.repository.QuestionRepository;
import com.kaidey.yakchatproject.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.kaidey.yakchatproject.entity.Image;
import com.kaidey.yakchatproject.dto.ImageDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private SubjectRepository subjectRepository; // SubjectRepository 추가

    // 질문 생성
    public Question createQuestion(QuestionDto questionDto) {
        try {
            Question question = new Question();
            question.setContent(questionDto.getContent());

            // 과목 설정
            question.setSubject(subjectRepository.findById(questionDto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found")));

            // 이미지 설정
            if (questionDto.getImages() != null) {
                for (ImageDto imageDto : questionDto.getImages()) {
                    Image image = new Image();
                    image.setData(imageDto.getData());
                    image.setFileName(imageDto.getFileName());
                    image.setQuestion(question); // 질문과 연관
                    question.getImages().add(image);
                }
            }

            return questionRepository.save(question); // 질문 저장
        } catch (Exception e) {
            throw new RuntimeException("Error creating question: " + e.getMessage());
        }
    }

    // 특정 질문 조회
    public Question getQuestionById(Long id) {
        try {
            return questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found"));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving question: " + e.getMessage());
        }
    }

    // 모든 질문 조회
    public List<Question> getAllQuestions() {
        try {
            return questionRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all questions: " + e.getMessage());
        }
    }

    // 과목 ID로 질문 조회
    public List<Question> getQuestionsBySubjectId(Long subjectId) {
        try {
            return questionRepository.findBySubjectId(subjectId);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving questions by subject ID: " + e.getMessage());
        }
    }

    // 질문 업데이트
    public Question updateQuestion(Long id, QuestionDto questionDto) {
        try {
            Question question = getQuestionById(id);
            Subject subject = subjectRepository.findById(questionDto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found")); // 과목 객체 조회

            question.setContent(questionDto.getContent());
            question.setSubject(subject); // 과목 객체 설정
            return questionRepository.save(question);
        } catch (Exception e) {
            throw new RuntimeException("Error updating question: " + e.getMessage());
        }
    }

    // 질문 삭제
    public void deleteQuestion(Long id) {
        try {
            questionRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting question: " + e.getMessage());
        }
    }
}