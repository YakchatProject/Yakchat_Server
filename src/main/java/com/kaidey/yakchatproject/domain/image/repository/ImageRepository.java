package com.kaidey.yakchatproject.domain.image.repository;

import com.kaidey.yakchatproject.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    //  중복 체크 메서드 추가
    boolean existsByUrl(String url);
}
