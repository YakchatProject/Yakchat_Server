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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

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
        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Answer answer = new Answer();
        answer.setContent(answerDto.getContent());
        answer.setIsAnonymous(answerDto.getIsAnonymous());
        answer.setQuestion(question);
        answer.setUser(user);

        if (answerDto.getImages() != null && !answerDto.getImages().isEmpty()) {
            if (answer.getImages() == null) {
                answer.setImages(new ArrayList<>());
            }

            for (ImageDto imageDto : answerDto.getImages()) {
                if (imageDto.getData() == null || imageDto.getFileName() == null) {
                    throw new RuntimeException("Invalid image data");
                }

                Image image = new Image();
                image.setData(imageDto.getData());
                image.setFileName(imageDto.getFileName());
                image.setAnswer(answer);
                answer.getImages().add(image);
            }
        }

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
        try {
            Answer answer = answerRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + id));

            // Update answer details
            answer.setContent(answerDto.getContent());
            answer.setIsAnonymous(answerDto.getIsAnonymous()); // 익명 여부 설정

            // Update question
            Question question = questionRepository.findById(answerDto.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + answerDto.getQuestionId()));
            answer.setQuestion(question);

            // Update images if provided
            if (answerDto.getImages() != null && !answerDto.getImages().isEmpty()) {
                answer.getImages().clear();
                for (ImageDto imageDto : answerDto.getImages()) {
                    if (imageDto.getData() == null || imageDto.getFileName() == null) {
                        throw new RuntimeException("Invalid image data for image with file name: " + imageDto.getFileName());
                    }
                    Image image = new Image();
                    image.setData(imageDto.getData());
                    image.setFileName(imageDto.getFileName());
                    image.setAnswer(answer);
                    answer.getImages().add(image);
                }
            }

            Answer updatedAnswer = answerRepository.save(answer);
            return convertToDto(updatedAnswer);
        } catch (Exception e) {
            throw new RuntimeException("Error updating answer: " + e.getMessage(), e);
        }
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
        answerDto.setIsAnonymous(answer.getIsAnonymous()); // 익명 여부 설정
        answerDto.setUserId(answer.getUser().getId());
        answerDto.setUserName(answer.getIsAnonymous() ? "" : answer.getUser().getUsername()); // 익명 여부에 따라 사용자 이름 설정
        answerDto.setQuestionId(answer.getQuestion().getId());
        answerDto.setCreatedAt(answer.getCreatedAt());
        answerDto.setModifiedAt(answer.getModifiedAt());
        answerDto.setLikeCount(answer.getLikes());
        answerDto.setImages(answer.getImages().stream()
                .map(image -> {
                    ImageDto imageDto = new ImageDto();
                    imageDto.setData(image.getData());
                    imageDto.setFileName(image.getFileName());
                    return imageDto;
                }).collect(Collectors.toList()));
        return answerDto;
    }
}