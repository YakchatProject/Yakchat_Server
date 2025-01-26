package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.AnswerDto;
import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.entity.*;
import com.kaidey.yakchatproject.exception.EntityNotFoundException;
import com.kaidey.yakchatproject.repository.AnswerRepository;
import com.kaidey.yakchatproject.repository.LikeRepository;
import com.kaidey.yakchatproject.repository.QuestionRepository;
import com.kaidey.yakchatproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kaidey.yakchatproject.util.ImageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.tika.mime.MimeTypeException;


@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ImageUtils imageUtils = new ImageUtils();


    @Autowired
    public AnswerService(AnswerRepository answerRepository,
                         QuestionRepository questionRepository,
                         UserRepository userRepository, LikeRepository likeRepository) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    // 답변 생성
    @Transactional
    public AnswerDto createAnswer(AnswerDto answerDto, Long userId) {
        // Question 찾기
        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        // User 찾기
        System.out.println("userId: " + userId);
        User user = userRepository.findById(answerDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Answer 객체 생성 및 세팅
        Answer answer = new Answer();
        answer.setContent(answerDto.getContent());
        answer.setQuestion(question);
        answer.setUser(user);

        // 이미지 처리
        if (answerDto.getImages() != null && !answerDto.getImages().isEmpty()) {
            for (ImageDto imageDto : answerDto.getImages()) {
                Image image = new Image();
                image.setUrl(imageDto.getUrl());
                image.setFileName(imageDto.getFileName());
                image.setAnswer(answer);
                answer.getImages().add(image);
            }
        }

        // 답변 저장
        Answer savedAnswer = answerRepository.save(answer);
        return convertToDto(savedAnswer);
    }



    // 특정 답변 조회
    @Transactional
    public AnswerDto getAnswerById(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found"));
        return convertToDto(answer);
    }

    // 특정 질문과 사용자에 대한 답변 조회
    @Transactional
    public List<AnswerDto> getAnswersByQuestionIdAndUserId(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Answer> answers = answerRepository.findByQuestionIdAndUserIdOrderByCreatedAtDesc(questionId, userId);

        if (answers.isEmpty()) {
            return List.of(); // Return an empty list if no answers are found
        }

        return answers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 모든 답변 조회
    @Transactional
    public List<AnswerDto> getAllAnswers() {
        return answerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 답변 업데이트
    @Transactional
    public AnswerDto updateAnswer(Long id, AnswerDto answerDto) {
        // Answer 찾기
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + id));

        // Answer 세팅
        answer.setContent(answerDto.getContent());

        // Question 찾기
        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + answerDto.getQuestionId()));
        answer.setQuestion(question);

        // 이미지 처리
        if (answerDto.getImages() != null && !answerDto.getImages().isEmpty()) {
            answer.getImages().clear(); // 기존 이미지를 제거하고 새 이미지로 교체
            for (ImageDto imageDto : answerDto.getImages()) {
                Image image = new Image();
                image.setUrl(imageDto.getUrl());
                image.setFileName(imageDto.getFileName());
                image.setAnswer(answer);
                answer.getImages().add(image);
            }
        }

        // 답변 저장
        Answer updatedAnswer = answerRepository.save(answer);
        return convertToDto(updatedAnswer);
    }



    @Transactional
    public List<AnswerDto> getAnswersByQuestionId(Long questionId) {
        List<Answer> answers = answerRepository.findByQuestionIdOrderByCreatedAtDesc(questionId);
        return answers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 답변 삭제
    @Transactional
    public void deleteAnswer(Long id) {
        answerRepository.deleteById(id);
    }

    @Transactional
    public void likeAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (likeRepository.findByUserIdAndAnswerId(userId, answerId).isEmpty()) {
            Like like = new Like();
            like.setUser(user); // Assuming User entity has a constructor with ID
            like.setAnswer(answer);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikeAnswer(Long answerId, Long userId) {
        Like like = likeRepository.findByUserIdAndAnswerId(userId, answerId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found"));
        likeRepository.delete(like);
    }

    @Transactional
    public long getAnswerLikeCount(Long answerId) {
        return likeRepository.countByAnswerId(answerId);
    }

    // Answer 엔티티를 AnswerDto로 변환
    private AnswerDto convertToDto(Answer answer) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setId(answer.getId());
        answerDto.setContent(answer.getContent());
        answerDto.setQuestionId(answer.getQuestion().getId());
        answerDto.setUserId(answer.getUser().getId());
        answerDto.setCreatedAt(answer.getCreatedAt());
        answerDto.setModifiedAt(answer.getModifiedAt());
        answerDto.setLikeCount(answer.getLikes());
        answerDto.setImages(imageUtils.convertToImageDtos(answer.getImages()));
        return answerDto;
    }
}