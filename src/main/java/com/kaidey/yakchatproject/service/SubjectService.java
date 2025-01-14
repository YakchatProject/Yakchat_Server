package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.SubjectDto;
import com.kaidey.yakchatproject.entity.Subject;
import com.kaidey.yakchatproject.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    // 과목 생성
    public Subject createSubject(SubjectDto subjectDto) {
        Subject subject = new Subject();
        subject.setName(subjectDto.getName());
        return subjectRepository.save(subject);
    }

    // 특정 과목 조회
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Subject not found"));
    }

    // 모든 과목 조회
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // 과목 업데이트
    public Subject updateSubject(Long id, SubjectDto subjectDto) {
        Subject subject = getSubjectById(id);
        subject.setName(subjectDto.getName());
        return subjectRepository.save(subject);
    }

    // 과목 삭제
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
}
