package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    // 추가적인 쿼리 메서드 정의 가능
}
