package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.dto.QuestionDto;
import com.kaidey.yakchatproject.entity.Question;
import com.kaidey.yakchatproject.service.QuestionService;
import com.kaidey.yakchatproject.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kaidey.yakchatproject.security.JwtTokenProvider;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final JwtTokenProvider jwtTokenProvider;

    // QuestionService와 JwtTokenProvider를 주입받도록 생성자 수정
    @Autowired
    public QuestionController(QuestionService questionService, JwtTokenProvider jwtTokenProvider) {
        this.questionService = questionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    //질문 생성
    @PostMapping
    public ResponseEntity<Question> createQuestion(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long subjectId,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7)); // "Bearer " 부분 제거
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(title);
        questionDto.setContent(content);
        questionDto.setSubjectId(subjectId);
        questionDto.setUserId(userId);
        // 이미지가 제공된 경우 처리
        if (images != null) {
            try {
                questionDto.setImages(ImageUtils.processImages(images));
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }
        }
        Question newQuestion = questionService.createQuestion(questionDto);
        return ResponseEntity.ok(newQuestion);
    }

    //특정 질문 조회
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable Long id) {
        QuestionDto question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }


    //모든 질문 조회
    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    //질문 업데이트
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long subjectId,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7)); // "Bearer " 부분 제거
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(title);
        questionDto.setContent(content);
        questionDto.setSubjectId(subjectId);
        questionDto.setUserId(userId);
        // 이미지가 제공된 경우 처리
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


    //질문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    //과목 ID로 질문 조회
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<QuestionDto>> getQuestionsBySubjectId(@PathVariable Long subjectId) {
        List<QuestionDto> questions = questionService.getQuestionsBySubjectId(subjectId);
        return ResponseEntity.ok(questions);
    }
}
