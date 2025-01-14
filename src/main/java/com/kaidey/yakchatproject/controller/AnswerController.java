package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.AnswerDto;
import com.kaidey.yakchatproject.service.AnswerService;
import com.kaidey.yakchatproject.util.ImageUtils;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AnswerController(AnswerService answerService, JwtTokenProvider jwtTokenProvider) {
        this.answerService = answerService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 답변 생성
    @PostMapping
    public ResponseEntity<AnswerDto> createAnswer(
            @RequestParam String content,
            @RequestParam Long questionId,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7)); // "Bearer " 부분 제거
        AnswerDto answerDto = new AnswerDto();
        answerDto.setContent(content);
        answerDto.setQuestionId(questionId);
        // 이미지가 제공된 경우 처리
        if (images != null) {
            try {
                answerDto.setImages(ImageUtils.processImages(images));
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }
        }
        AnswerDto newAnswer = answerService.createAnswer(answerDto, userId);
        return ResponseEntity.ok(newAnswer);
    }

    // 특정 답변 조회
    @GetMapping("/{id}")
    public ResponseEntity<AnswerDto> getAnswerById(@PathVariable Long id) {
        AnswerDto answer = answerService.getAnswerById(id);
        return ResponseEntity.ok(answer);
    }

    // 모든 답변 조회
    @GetMapping
    public ResponseEntity<List<AnswerDto>> getAllAnswers() {
        List<AnswerDto> answers = answerService.getAllAnswers();
        return ResponseEntity.ok(answers);
    }

    // 답변 업데이트
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<AnswerDto> updateAnswer(
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam Long questionId,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7)); // "Bearer " 부분 제거
        AnswerDto answerDto = new AnswerDto();
        answerDto.setContent(content);
        answerDto.setQuestionId(questionId);
        // 이미지가 제공된 경우 처리
        if (images != null) {
            try {
                answerDto.setImages(ImageUtils.processImages(images));
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }
        }
        AnswerDto updatedAnswer = answerService.updateAnswer(id, answerDto);
        return ResponseEntity.ok(updatedAnswer);
    }

    // 답변 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/likeCount")
    public ResponseEntity<Long> getAnswerLikeCount(@PathVariable Long id) {
        long likeCount = answerService.getAnswerLikeCount(id);
        return ResponseEntity.ok(likeCount);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeAnswer(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7)); // "Bearer " 부분 제거
        answerService.likeAnswer(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<Void> unlikeAnswer(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7)); // "Bearer " 부분 제거
        answerService.unlikeAnswer(id, userId);
        return ResponseEntity.ok().build();
    }
}