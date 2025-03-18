package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.controller.AnswerController;
import com.kaidey.yakchatproject.dto.AnswerDto;
import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.entity.*;
import com.kaidey.yakchatproject.exception.EntityNotFoundException;
import com.kaidey.yakchatproject.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kaidey.yakchatproject.util.ImageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.LinkedHashMap;



@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ImageUtils imageUtils = new ImageUtils();
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(AnswerController.class);


    @Autowired
    public AnswerService(AnswerRepository answerRepository,
                         QuestionRepository questionRepository,
                         UserRepository userRepository, LikeRepository likeRepository,
                         ImageRepository imageRepository, ImageService imageService,
                         UserService userService) {
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.imageRepository = imageRepository;
        this.imageService = imageService;
        this.userService = userService;
    }

    // 답변 생성
    @Transactional
    public AnswerDto createAnswer(AnswerDto answerDto, List<MultipartFile> images) throws IOException {
        // Question과 User 찾기
        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        User user = userRepository.findById(answerDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Answer 객체 생성
        Answer answer = new Answer();
        answer.setContent(answerDto.getContent());
        answer.setQuestion(question);
        answer.setUser(user);

        // 이미지가 있으면 미리 리스트에 추가
        List<Image> imageList = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            imageList = imageService.saveImages(images, answer);
            answer.setImages(imageList);
        }

        // Answer 저장
        Answer savedAnswer = answerRepository.save(answer);

        log.info("Saved answer ID: {}, Images count: {}", savedAnswer.getId(), savedAnswer.getImages() != null ? savedAnswer.getImages().size() : "null");

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
    public AnswerDto updateAnswer(Long id, AnswerDto answerDto,  List<MultipartFile> images, List<Long> deleteImageIds) throws IOException {
        // Answer 찾기
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Answer not found with id: " + id));

        answer.setContent(answerDto.getContent());

        // Question 찾기 및 설정
        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + answerDto.getQuestionId()));
        answer.setQuestion(question);

        // 삭제할 이미지가 있다면 제거
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            List<Image> imagesToDelete = imageRepository.findAllById(deleteImageIds);
            for (Image image : imagesToDelete) {
                answer.getImages().remove(image);
                imageRepository.delete(image); // DB에서 삭제
            }
        }

        // 새로운 이미지 추가
        if (images != null && !images.isEmpty()) {
            List<Image> savedImages = imageService.saveImages(images, answer);
            answer.getImages().addAll(savedImages);
        }

        answer.updateModifiedAt();

        // DTO 변환 후 반환
        return convertToDto(answerRepository.save(answer));
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
    public void acceptAnswer(Long answerId, User user) {
        // 답변 찾기
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 답변이 존재하지 않습니다."));

        // 질문 찾기
        Question question = answer.getQuestion();

        // 질문 작성자만 답변을 채택할 수 있도록 제한
        if (!question.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("질문 작성자만 답변을 채택할 수 있습니다.");
        }

        // 이미 채택된 답변이 있는지 확인
        if (answerRepository.existsByQuestionIdAndIsAcceptedTrue(question.getId())) {
            throw new IllegalStateException("이미 채택된 답변이 있습니다. 답변을 변경할 수 없습니다.");
        }

        // 답변 채택
        answer.setIsAccepted(true);
        answerRepository.save(answer);

        // 답변 작성자의 활동 점수 업데이트 (예: 채택된 답변 개수 증가)
        userService.updateUserActivity(answer.getUser(), 0, 1, 0, 0, 0);
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
        answerDto.setAccepted(answer.getIsAccepted());
        int totalSteps = answer.getContent().split("\n\n|\r\n\r\n").length;
        answerDto.setImages(imageUtils.convertToImageMap(answer.getImages(), totalSteps));

        return answerDto;
    }

}