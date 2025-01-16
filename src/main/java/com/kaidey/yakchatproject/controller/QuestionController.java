package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.dto.QuestionDto;
import com.kaidey.yakchatproject.service.QuestionService;
import com.kaidey.yakchatproject.util.ImageUtils;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public QuestionController(QuestionService questionService, JwtTokenProvider jwtTokenProvider) {
        this.questionService = questionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 질문 생성
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<QuestionDto> createQuestion(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam("isAnonymous") Boolean isAnonymous,
            @RequestParam("images") MultipartFile[] images,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(title);
        questionDto.setContent(content);
        questionDto.setSubjectId(subjectId);
        questionDto.setIsAnonymous(isAnonymous);
        questionDto.setUserId(userId);

        if (images != null && images.length > 0) {
            try {
                List<ImageDto> imageDtos = ImageUtils.processImages(images);
                questionDto.setImages(imageDtos);
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }
        }

        QuestionDto newQuestion = questionService.createQuestion(questionDto);
        return ResponseEntity.ok(newQuestion);
    }

    // 질문 조회
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable Long id) {
        QuestionDto question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    // 모든 질문 조회
    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    // 질문 수정
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long subjectId,
            @RequestParam Boolean isAnonymous,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(title);
        questionDto.setContent(content);
        questionDto.setSubjectId(subjectId);
        questionDto.setIsAnonymous(isAnonymous);
        questionDto.setUserId(userId);
        if (images != null) {
            try {
                questionDto.setImages(ImageUtils.processImages(images));
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }
        }
        QuestionDto updatedQuestionDto = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(updatedQuestionDto);
    }

    // 질문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    // 과목 ID로 질문 조회
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<QuestionDto>> getQuestionsBySubjectId(@PathVariable Long subjectId) {
        List<QuestionDto> questions = questionService.getQuestionsBySubjectId(subjectId);
        return ResponseEntity.ok(questions);
    }

    // 질문 좋아요 수 조회
    @GetMapping("/{id}/likeCount")
    public ResponseEntity<Long> getQuestionLikeCount(@PathVariable Long id) {
        long likeCount = questionService.getQuestionLikeCount(id);
        return ResponseEntity.ok(likeCount);
    }

    // 질문 좋아요
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeQuestion(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        questionService.likeQuestion(id, userId);
        return ResponseEntity.ok().build();
    }

    // 질문 좋아요 취소
    @PostMapping("/{id}/unlike")
    public ResponseEntity<Void> unlikeQuestion(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        questionService.unlikeQuestion(id, userId);
        return ResponseEntity.ok().build();
    }

    // 질문 검색
    @GetMapping("/search")
    public ResponseEntity<List<QuestionDto>> searchQuestions(@RequestParam String keyword) {
        List<QuestionDto> questions = questionService.searchQuestions(keyword);
        return ResponseEntity.ok(questions);
    }
}