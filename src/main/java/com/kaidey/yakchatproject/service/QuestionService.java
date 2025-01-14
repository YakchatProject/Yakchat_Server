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
    }


    // 특정 질문 조회
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElseThrow(() -> new RuntimeException("Question not found"));
    }

    // 모든 질문 조회
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // 과목 ID로 질문 조회
    public List<Question> getQuestionsBySubjectId(Long subjectId) {
        return questionRepository.findBySubjectId(subjectId);
    }

    // 질문 업데이트
    public Question updateQuestion(Long id, QuestionDto questionDto) {
        Question question = getQuestionById(id);
        Subject subject = subjectRepository.findById(questionDto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found")); // 과목 객체 조회

        question.setContent(questionDto.getContent());
        question.setSubject(subject); // 과목 객체 설정
        return questionRepository.save(question);
    }

    // 질문 삭제
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }


}
