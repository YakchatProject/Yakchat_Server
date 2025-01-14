package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.entity.Answer;
import com.kaidey.yakchatproject.service.AnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {
    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    //모든 답변 반환
    @GetMapping
    public List<Answer> getAllAnswers() {
        return answerService.findAll();
    }

    //주어진 id에 해당하는 답변 반환
    @GetMapping("/{id}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable Long id) {
        return answerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 답변 생성
    @PostMapping
    public Answer createAnswer(@RequestBody Answer answer) {
        return answerService.save(answer);
    }

    //주어진 id에 해당하는 답변 수정
    @PutMapping("/{id}")
    public ResponseEntity<Answer> updateAnswer(@PathVariable Long id, @RequestBody Answer answer) {
        answer.setId(id);
        return ResponseEntity.ok(answerService.save(answer));
    }

    //주어진 id에 해당하는 답변 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
