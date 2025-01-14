package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.dto.SubjectDto;
import com.kaidey.yakchatproject.entity.Subject;
import com.kaidey.yakchatproject.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;


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
    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Subject not found"));
        return convertToDto(subject);
    }

    // 모든 과목 조회
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 과목 업데이트
    public Subject updateSubject(Long id, SubjectDto subjectDto) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Subject not found"));
        subject.setName(subjectDto.getName());
        return subjectRepository.save(subject);
    }

    // 과목 삭제
    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    private SubjectDto convertToDto(Subject subject) {
        SubjectDto subjectDto = new SubjectDto();
        subjectDto.setId(subject.getId());
        subjectDto.setName(subject.getName());
//        subjectDto.setQuestionIds(subject.getQuestions().stream().map(Question::getId).collect(Collectors.toList()));
//        subjectDto.setImageIds(subject.getImages().stream().map(Image::getId).collect(Collectors.toList()));
        return subjectDto;
    }
}
