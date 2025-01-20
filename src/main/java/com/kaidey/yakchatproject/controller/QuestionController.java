package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.ImageDto;
import com.kaidey.yakchatproject.dto.QuestionDto;
import com.kaidey.yakchatproject.service.QuestionService;
import com.kaidey.yakchatproject.util.ImageUtils;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.Base64;
import java.io.IOException;
import java.util.List;
import java.io.File;
@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${upload.dir}")
    private String uploadDir;


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
            @RequestParam(value = "images", required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(title);
        questionDto.setContent(content);
        questionDto.setSubjectId(subjectId);
        questionDto.setIsAnonymous(isAnonymous);
        questionDto.setUserId(userId);

        // 이미지 데이터 처리
        if (images != null && images.length > 0) {
            try {
                List<ImageDto> imageDtos = new ArrayList<>();
                for (MultipartFile image : images) {
                    String base64Image = new String(image.getBytes());
                    String imagePath = ImageUtils.saveBase64Image(base64Image, uploadDir);
                    ImageDto imageDto = new ImageDto();
                    imageDto.setUrl("/images/" + new File(imagePath).getName());
                    imageDtos.add(imageDto);
                }
                questionDto.setImages(imageDtos);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(null);
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

    // 모든 질문 조회(오래된 순)
    @GetMapping("/oldest")
    public ResponseEntity<List<QuestionDto>> getAllQuestionsOldestFirst() {
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    // 과목 ID로 질문 조회
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<QuestionDto>> getQuestionsBySubjectId(@PathVariable Long subjectId) {
        List<QuestionDto> questions = questionService.getQuestionsBySubjectId(subjectId);
        return ResponseEntity.ok(questions);
    }

    // 과목 ID로 질문 조회(오래된 순)
    @GetMapping("/subject/{subjectId}/oldest")
    public ResponseEntity<List<QuestionDto>> getQuestionsBySubjectIdOldestFirst(@PathVariable Long subjectId) {
        List<QuestionDto> questions = questionService.getQuestionsBySubjectIdOldestFirst(subjectId);
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

        // 이미지 데이터 처리
        if (images != null && images.length > 0) {
            try {
                List<ImageDto> imageDtos = new ArrayList<>();
                for (MultipartFile image : images) {
                    String base64Image = new String(image.getBytes());
                    String imagePath = ImageUtils.saveBase64Image(base64Image, uploadDir);
                    ImageDto imageDto = new ImageDto();
                    imageDto.setUrl("/images/" + new File(imagePath).getName());
                    imageDtos.add(imageDto);
                }
                questionDto.setImages(imageDtos);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(null);
            }
        }

        QuestionDto updatedQuestion = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(updatedQuestion);
    }


    // 질문 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
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
    @DeleteMapping("/{id}/like")
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