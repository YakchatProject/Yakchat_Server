package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.QuestionDto;
import com.kaidey.yakchatproject.entity.*;
import com.kaidey.yakchatproject.repository.*;
import com.kaidey.yakchatproject.exception.EntityNotFoundException;
import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.util.ImageUtils;
import com.kaidey.yakchatproject.dto.QuestionWithAnswersDto;
import com.kaidey.yakchatproject.dto.AnswerDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final ImageUtils imageUtils = new ImageUtils();

    @Autowired
    public QuestionService(QuestionRepository questionRepository, SubjectRepository subjectRepository,
                           UserRepository userRepository, LikeRepository likeRepository,UserService userService,
                           AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
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
//        question.setIsAnonymous(questionDto.getIsAnonymous());
        question.setSubject(subject);
        question.setUser(user);
        userService.updateUserActivity(user, 1, 0, 0, 0, 0);

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

    // 질문 + 답변 조회
    @Transactional
    public QuestionWithAnswersDto getQuestionWithAnswers(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        // 질문 조회수 증가
        question.incrementViewCount();
        questionRepository.save(question);

        // 질문 DTO 생성
        QuestionWithAnswersDto questionDto = convertToQuestionWithAnswersDto(question);

        // 해당 질문의 답변 목록 추가
        List<AnswerDto> answerDtos = answerRepository.findByQuestionIdOrderByCreatedAtDesc(questionId).stream()
                .map(this::convertAnswerToDto)
                .collect(Collectors.toList());

        questionDto.setAnswers(answerDtos);
        return questionDto;
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
//        question.setIsAnonymous(questionDto.getIsAnonymous());

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
//        questionDto.setIsAnonymous(question.getIsAnonymous());
        questionDto.setSubjectId(question.getSubject().getId());
        questionDto.setSubjectName(question.getSubject().getName()); // Set subject name
        questionDto.setUserId(question.getUser().getId());
        questionDto.setUserName(question.getUser().getUsername()); // Set username based on anonymity
        questionDto.setCreatedAt(question.getCreatedAt());
        questionDto.setUpdatedAt(question.getModifiedAt());
        questionDto.setLikeCount(question.getLikes());
        questionDto.setViewCount(question.getViewCount());
        questionDto.setImages(imageUtils.convertToImageDtos(question.getImages()));

        return questionDto;
    }

    private AnswerDto convertAnswerToDto(Answer answer) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.setId(answer.getId());
        answerDto.setContent(answer.getContent());
        answerDto.setQuestionId(answer.getQuestion().getId());
        answerDto.setUserId(answer.getUser().getId());
        answerDto.setUserName(answer.getUser().getUsername());
        answerDto.setCreatedAt(answer.getCreatedAt());
        answerDto.setModifiedAt(answer.getModifiedAt());
        answerDto.setLikeCount(answer.getLikes());
        answerDto.setAccepted(answer.getIsAccepted());

        // 이미지 추가
        int totalSteps = answer.getContent().split("\n\n|\r\n\r\n").length;
        answerDto.setImages(imageUtils.convertToImageMap(answer.getImages(), totalSteps));

        return answerDto;
    }

    private QuestionWithAnswersDto convertToQuestionWithAnswersDto(Question question) {
        QuestionWithAnswersDto dto = new QuestionWithAnswersDto();
        dto.setId(question.getId());
        dto.setTitle(question.getTitle());
        dto.setContent(question.getContent());
//        dto.setIsAnonymous(question.getIsAnonymous());
        dto.setSubjectId(question.getSubject().getId());
        dto.setSubjectName(question.getSubject().getName());
        dto.setUserId(question.getUser().getId());
        dto.setUserName(question.getUser().getUsername());
        dto.setCreatedAt(question.getCreatedAt());
        dto.setUpdatedAt(question.getModifiedAt());
        dto.setLikeCount(question.getLikes());
        dto.setViewCount(question.getViewCount());
        dto.setImages(imageUtils.convertToImageDtos(question.getImages()));
        return dto;
    }



}