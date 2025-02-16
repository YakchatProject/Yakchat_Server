package com.kaidey.yakchatproject.repository;

import com.kaidey.yakchatproject.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllById(List<Long> ids);

    // 추가적인 쿼리 메서드 정의 가능
}
