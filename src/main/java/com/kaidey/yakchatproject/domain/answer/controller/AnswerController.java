package com.kaidey.yakchatproject.domain.answer.controller;

import com.kaidey.yakchatproject.domain.question.controller.QuestionController;
import com.kaidey.yakchatproject.domain.user.entity.User;
import com.kaidey.yakchatproject.domain.user.service.UserService;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import com.kaidey.yakchatproject.domain.answer.dto.AnswerDto;
import com.kaidey.yakchatproject.domain.answer.service.AnswerService;
import com.kaidey.yakchatproject.global.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {


    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);


    private final AnswerService answerService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AnswerController(AnswerService answerService, JwtTokenProvider jwtTokenProvider,
                            UserService userService) {
        this.answerService = answerService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService ;
    }

    // 답변 생성
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, Object>> createAnswer(
            @RequestParam("content") String content,
            @RequestParam("questionId") Long questionId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        AnswerDto answerDto = new AnswerDto(content, questionId, userId);

        try {
            AnswerDto newAnswer = answerService.createAnswer(answerDto, images);

            Map<String, Object> response = new HashMap<>();
            response.put("answerId", newAnswer.getId());
            response.put("content", newAnswer.getContent());
            response.put("questionId", newAnswer.getQuestionId());
            response.put("userId", newAnswer.getUserId());
            response.put("images", newAnswer.getImages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    // 특정 답변 조회
    @GetMapping("/{id}")
    public ResponseEntity<AnswerDto> getAnswerById(@PathVariable Long id) {
        AnswerDto answer = answerService.getAnswerById(id);
        return ResponseEntity.ok(answer);
    }

    // 특정 질문과 사용자에 대한 답변 조회
//    @GetMapping("/question/{questionId}/user")
//    public ResponseEntity<?> getAnswersByQuestionIdAndUserId(
//            @PathVariable Long questionId,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        try {
//            if (userDetails == null) {
//                logger.warn("Authentication failed: UserDetails is null.");
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body("User is not authenticated.");
//            }
//
//            Long userId;
//            if (userDetails instanceof User) {
//                userId = ((User) userDetails).getId();
//            } else {
//                logger.error("UserDetails is not an instance of User.");
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Internal server error.");
//            }
//            logger.info("Fetching answers for questionId: {} and userId: {}", questionId, userId);
//
//            List<AnswerDto> answerDtos = answerService.getAnswersByQuestionIdAndUserId(questionId, userId);
//            return ResponseEntity.ok(answerDtos);
//        } catch (IllegalArgumentException e) {
//            logger.error("Invalid argument: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Invalid argument: " + e.getMessage());
//        } catch (Exception e) {
//            logger.error("Unexpected error occurred while fetching answers: {}", e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An unexpected error occurred.");
//        }
//    }
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
    public ResponseEntity<Map<String, Object>> updateAnswer(
            @PathVariable Long id,
            @RequestParam("content") String content,
            @RequestParam("questionId") Long questionId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        AnswerDto answerDto = new AnswerDto(content, questionId, userId);

        try {
            AnswerDto updatedAnswer = answerService.updateAnswer(id, answerDto, images, deleteImageIds);

            Map<String, Object> response = new HashMap<>();
            response.put("answerId", updatedAnswer.getId());
            response.put("content", updatedAnswer.getContent());
            response.put("questionId", updatedAnswer.getQuestionId());
            response.put("userId", updatedAnswer.getUserId());
            response.put("images", updatedAnswer.getImages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    // 답변 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }

    // 답변 채택
    @PostMapping("/{answerId}/accept")
    public ResponseEntity<String> acceptAnswer(
            @PathVariable Long answerId,
            @RequestHeader("Authorization") String token) {

        // JWT 토큰에서 유저 ID 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        User user = userService.getUserById(userId);

        try {
            answerService.acceptAnswer(answerId, user);
            return ResponseEntity.ok("답변이 채택되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
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