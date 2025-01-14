package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.AnswerDto;
import com.kaidey.yakchatproject.entity.Answer;
import com.kaidey.yakchatproject.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kaidey.yakchatproject.dto.ImageDto;
import java.util.ArrayList;
import com.kaidey.yakchatproject.util.ImageUtils;
import java.util.List;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.kaidey.yakchatproject.security.JwtTokenProvider;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;


    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ResponseEntity<Answer> createAnswer(
            @RequestParam String content,
            @RequestParam Long questionId,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7)); // "Bearer " 부분 제거
        AnswerDto answerDto = new AnswerDto();
        answerDto.setContent(content);
        answerDto.setQuestionId(questionId);
        answerDto.setUserId(userId);
        // Handle images if provided
        if (images != null) {
            try {
                answerDto.setImages(ImageUtils.processImages(images));
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }
        }
        Answer newAnswer = answerService.createAnswer(answerDto);
        return ResponseEntity.ok(newAnswer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable Long id) {
        Answer answer = answerService.getAnswerById(id);
        return ResponseEntity.ok(answer);
    }

    @GetMapping
    public ResponseEntity<List<Answer>> getAllAnswers() {
        List<Answer> answers = answerService.getAllAnswers();
        return ResponseEntity.ok(answers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Answer> updateAnswer(@PathVariable Long id, @RequestBody AnswerDto answerDto) {
        Answer updatedAnswer = answerService.updateAnswer(id, answerDto);
        return ResponseEntity.ok(updatedAnswer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }
}