package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // 추가적인 쿼리 메서드 정의 가능
}
