package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.QuestionDto;
import com.kaidey.yakchatproject.entity.*;
import com.kaidey.yakchatproject.repository.LikeRepository;
import com.kaidey.yakchatproject.repository.QuestionRepository;
import com.kaidey.yakchatproject.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.repository.LikeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kaidey.yakchatproject.repository.UserRepository;
import com.kaidey.yakchatproject.exception.EntityNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.apache.tika.mime.MimeTypeException;
import com.kaidey.yakchatproject.util.ImageUtils;


@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ImageUtils imageUtils = new ImageUtils();

    @Autowired
    public QuestionService(QuestionRepository questionRepository, SubjectRepository subjectRepository,
                           UserRepository userRepository, LikeRepository likeRepository) {
        this.questionRepository = questionRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    // 질문 생성
    @Transactional
    public QuestionDto createQuestion(QuestionDto questionDto) {
        // Subject와 User 찾기
        Subject subject = subjectRepository.findById(questionDto.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        User user = userRepository.findById(questionDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Question 객체 생성 및 세팅
        Question question = new Question();
        question.setTitle(questionDto.getTitle());
        question.setContent(questionDto.getContent());
        question.setIsAnonymous(questionDto.getIsAnonymous());
        question.setSubject(subject);
        question.setUser(user);

        // 이미지 처리
        if (questionDto.getImages() != null && !questionDto.getImages().isEmpty()) {
            for (ImageDto imageDto : questionDto.getImages()) {
                Image image = new Image();
                image.setUrl(imageDto.getUrl());
                image.setFileName(imageDto.getFileName());
                image.setQuestion(question);
                question.getImages().add(image);
            }
        }

        // 질문 저장
        Question savedQuestion = questionRepository.save(question);
        return convertToDto(savedQuestion);
    }



    // 특정 질문 조회
    @Transactional
    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        question.incrementViewCount(); // Increment view count
        questionRepository.save(question); //
        return convertToDto(question);
    }

    // 모든 질문 조회
    @Transactional
    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 모든 질문 조회 (오래된 순)
    @Transactional
    public List<QuestionDto> getAllQuestionsOldestFirst() {
        return questionRepository.findByOrderByCreatedAtAsc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    // 과목 ID로 질문 조회
    @Transactional
    public List<QuestionDto> getQuestionsBySubjectId(Long subjectId) {
        return questionRepository.findBySubjectIdOrderByCreatedAtDesc(subjectId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 과목 ID로 질문 조회 (오래된 순)
    @Transactional
    public List<QuestionDto> getQuestionsBySubjectIdOldestFirst(Long subjectId) {
        return questionRepository.findBySubjectIdOrderByCreatedAtAsc(subjectId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 최신 질문 조회 5개
    @Transactional
    public List<QuestionDto> getLatestQuestions() {
        return questionRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 과목 ID로 최신 질문 조회 5개
    @Transactional
    public List<QuestionDto> getLatestQuestionsBySubjectId(Long subjectId) {
        return questionRepository.findTop5BySubjectIdOrderByCreatedAtDesc(subjectId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }



    // 질문 업데이트
    @Transactional
    public QuestionDto updateQuestion(Long id, QuestionDto questionDto) {
        // Question 찾기
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        // Subject 찾기
        Subject subject = subjectRepository.findById(questionDto.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        question.setSubject(subject);

        // Question 세팅
        question.setTitle(questionDto.getTitle());
        question.setContent(questionDto.getContent());
        question.setIsAnonymous(questionDto.getIsAnonymous());

        // 이미지 처리
        if (questionDto.getImages() != null && !questionDto.getImages().isEmpty()) {
            question.getImages().clear(); // 기존 이미지를 지우고 새 이미지로 업데이트
            for (ImageDto imageDto : questionDto.getImages()) {
                Image image = new Image();
                image.setUrl(imageDto.getUrl());
                image.setFileName(imageDto.getFileName());
                image.setQuestion(question);
                question.getImages().add(image);
            }
        }

        // 질문 저장
        Question updatedQuestion = questionRepository.save(question);
        return convertToDto(updatedQuestion);
    }



    // 질문 삭제
    @Transactional
    public void deleteQuestion(Long id) {
        // 존재하지 않는 질문 ID일 경우 예외 발생
        questionRepository.deleteById(id);
    }

    // 질문 좋아요
    @Transactional
    public void likeQuestion(Long questionId, Long userId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (likeRepository.findByUserIdAndQuestionId(userId, questionId).isEmpty()) {
            Like like = new Like();
            like.setUser(user);
            like.setQuestion(question);
            likeRepository.save(like);
        }
    }

    // 질문 좋아요 취소
    @Transactional
    public void unlikeQuestion(Long questionId, Long userId) {
        List<Like> likes = likeRepository.findByUserIdAndQuestionId(userId, questionId);
        if (likes.isEmpty()) {
            throw new EntityNotFoundException("Like not found");
        }
        likeRepository.deleteAll(likes);
    }

    // 질문 좋아요 수 조회
    @Transactional
    public long getQuestionLikeCount(Long questionId) {
        return likeRepository.countByQuestionId(questionId);
    }

    // 질문 검색
    @Transactional
    public List<QuestionDto> searchQuestions(String keyword) {
        if (keyword.length() > 100) {
            throw new IllegalArgumentException("Keyword length must be 100 characters or less");
        }
        return questionRepository.findByTitleContainingOrContentContaining(keyword, keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Question 엔티티를 QuestionDto로 변환
    private QuestionDto convertToDto(Question question) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setId(question.getId());
        questionDto.setTitle(question.getTitle());
        questionDto.setContent(question.getContent());
        questionDto.setIsAnonymous(question.getIsAnonymous());
        questionDto.setSubjectId(question.getSubject().getId());
        questionDto.setSubjectName(question.getSubject().getName()); // Set subject name
        questionDto.setUserId(question.getUser().getId());
        questionDto.setUserName(question.getIsAnonymous() ? "" : question.getUser().getUsername()); // Set username based on anonymity
        questionDto.setCreatedAt(question.getCreatedAt());
        questionDto.setUpdatedAt(question.getModifiedAt());
        questionDto.setLikeCount(question.getLikes());
        questionDto.setViewCount(question.getViewCount());
        questionDto.setImages(question.getImages().stream()
                .map(image -> {
                    ImageDto imageDto = new ImageDto();
                    imageDto.setFileName(image.getFileName());
                    imageDto.setUrl(image.getUrl());
                    imageDto.setMime(image.getMime());
                    return imageDto;
                }).collect(Collectors.toList()));
        return questionDto;
    }


}