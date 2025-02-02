package com.kaidey.yakchatproject.controller;

import com.kaidey.yakchatproject.dto.SubjectDto;
import com.kaidey.yakchatproject.entity.Subject;
import com.kaidey.yakchatproject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/{id}")
    public ResponseEntity<SubjectDto> getSubjectById(@PathVariable Long id) {
        SubjectDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    @GetMapping
    public ResponseEntity<List<SubjectDto>> getAllSubjects() {
        List<SubjectDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody SubjectDto subjectDto) {
        Subject newSubject = subjectService.createSubject(subjectDto);
        return ResponseEntity.ok(newSubject);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody SubjectDto subjectDto) {
        Subject updatedSubject = subjectService.updateSubject(id, subjectDto);
        return ResponseEntity.ok(updatedSubject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
