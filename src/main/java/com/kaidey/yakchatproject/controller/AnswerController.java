package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.AnswerDto;
import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.service.AnswerService;
import com.kaidey.yakchatproject.util.ImageUtils;
import com.kaidey.yakchatproject.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.kaidey.yakchatproject.dto.ImageDto;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

import java.io.File;
import java.util.ArrayList;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    private final AnswerService answerService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${upload.dir}")
    private String uploadDir;

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);


    @Autowired
    public AnswerController(AnswerService answerService, JwtTokenProvider jwtTokenProvider) {
        this.answerService = answerService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 답변 생성
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<AnswerDto> createAnswer(
            @RequestParam String content,
            @RequestParam Long questionId,
            @RequestParam Boolean isAnonymous,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        AnswerDto answerDto = new AnswerDto();
        answerDto.setContent(content);
        answerDto.setQuestionId(questionId);
        answerDto.setIsAnonymous(isAnonymous);

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
                answerDto.setImages(imageDtos);
            } catch (IOException e) {
                e.printStackTrace();
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

    // 특정 질문과 사용자에 대한 답변 조회
    @GetMapping("/question/{questionId}/user")
    public ResponseEntity<?> getAnswersByQuestionIdAndUserId(
            @PathVariable Long questionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                logger.warn("Authentication failed: UserDetails is null.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("User is not authenticated.");
            }

            Long userId;
            if (userDetails instanceof User) {
                userId = ((User) userDetails).getId();
            } else {
                logger.error("UserDetails is not an instance of User.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Internal server error.");
            }
            logger.info("Fetching answers for questionId: {} and userId: {}", questionId, userId);

            List<AnswerDto> answerDtos = answerService.getAnswersByQuestionIdAndUserId(questionId, userId);
            return ResponseEntity.ok(answerDtos);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid argument: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching answers: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred.");
        }
    }
    // 모든 답변 조회
    @GetMapping
    public ResponseEntity<List<AnswerDto>> getAllAnswers() {
        List<AnswerDto> answers = answerService.getAllAnswers();
        return ResponseEntity.ok(answers);
    }

    // 특정 질문에 해당하는 답변 조회
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<AnswerDto>> getAnswersByQuestionId(@PathVariable Long questionId) {
        List<AnswerDto> answers = answerService.getAnswersByQuestionId(questionId);
        return ResponseEntity.ok(answers);
    }




    // 답변 업데이트
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<AnswerDto> updateAnswer(
            @PathVariable Long id,
            @RequestParam String content,
            @RequestParam Long questionId,
            @RequestParam Boolean isAnonymous,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        AnswerDto answerDto = new AnswerDto();
        answerDto.setContent(content);
        answerDto.setQuestionId(questionId);
        answerDto.setIsAnonymous(isAnonymous);

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
                answerDto.setImages(imageDtos);
            } catch (IOException e) {
                e.printStackTrace();
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

    // 답변 좋아요
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeAnswer(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        answerService.likeAnswer(id, userId);
        return ResponseEntity.ok().build();
    }

    // 답변 좋아요 취소
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeAnswer(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        answerService.unlikeAnswer(id, userId);
        return ResponseEntity.ok().build();
    }



}