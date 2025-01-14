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
import org.springframework.transaction.annotation.Transactional;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.repository.UserRepository;
import com.kaidey.yakchatproject.exception.EntityNotFoundException;

import java.util.List;
import java.util.ArrayList;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, SubjectRepository subjectRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    // 질문 생성
    @Transactional
    public Question createQuestion(QuestionDto questionDto) {

        if (questionDto.getSubjectId() == null) {
            throw new IllegalArgumentException("Subject ID must not be null");
        }

        Subject subject = subjectRepository.findById(questionDto.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        User user = userRepository.findById(questionDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Question question = new Question();
        question.setTitle(questionDto.getTitle());
        question.setContent(questionDto.getContent());
        question.setSubject(subject);
        question.setUser(user);
        // 이미지 리스트가 비어있지 않으면 이미지 추가
        if (questionDto.getImages() != null && !questionDto.getImages().isEmpty()) {
            if (question.getImages() == null) {
                question.setImages(new ArrayList<>());
            }

            for (ImageDto imageDto : questionDto.getImages()) {
                if (imageDto.getData() == null || imageDto.getFileName() == null) {
                    throw new RuntimeException("Invalid image data");
                }

                Image image = new Image();
                image.setData(imageDto.getData());
                image.setFileName(imageDto.getFileName());
                image.setQuestion(question);
                question.getImages().add(image);
            }
        }

        return questionRepository.save(question);
    }

    // 특정 질문 조회
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
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
    @Transactional
    public Question updateQuestion(Long id, QuestionDto questionDto) {
        Question question = getQuestionById(id);

        // 과목 확인 및 설정
        Subject subject = subjectRepository.findById(questionDto.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        question.setContent(questionDto.getContent());
        question.setSubject(subject); // 과목 객체 설정

        return questionRepository.save(question);
    }

    // 질문 삭제
    @Transactional
    public void deleteQuestion(Long id) {
        // 존재하지 않는 질문 ID일 경우 예외 발생
        questionRepository.deleteById(id);
    }
}
