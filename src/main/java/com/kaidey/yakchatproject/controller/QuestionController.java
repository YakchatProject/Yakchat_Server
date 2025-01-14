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

    @Autowired
    public QuestionController(QuestionService questionService, JwtTokenProvider jwtTokenProvider) {
        this.questionService = questionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Question question = questionService.getQuestionById(id);
        return ResponseEntity.ok(question);
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Question> updateQuestion(
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
        Question updatedQuestion = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Question>> getQuestionsBySubjectId(@PathVariable Long subjectId) {
        List<Question> questions = questionService.getQuestionsBySubjectId(subjectId);
        return ResponseEntity.ok(questions);
    }
}
