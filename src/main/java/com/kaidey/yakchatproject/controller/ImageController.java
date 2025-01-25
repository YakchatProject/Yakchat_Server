package com.kaidey.yakchatproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.FileNotFoundException;
import org.springframework.http.HttpStatus;
import com.kaidey.yakchatproject.service.ImageService;
import com.kaidey.yakchatproject.dto.ImageDto;


@RestController
@RequestMapping("/images")
public class ImageController {

    @Value("${upload.dir}") // 이미지 저장 경로
    private String uploadDir;

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }


    @GetMapping("/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        try {
            // 저장된 경로에서 이미지 파일을 검색
            Path imagePath = Paths.get(uploadDir).resolve(imageName).normalize();
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // MIME 타입 자동 추론
                String contentType = Files.probeContentType(imagePath);
                if (contentType == null) {
                    contentType = "application/octet-stream"; // 기본 MIME 타입
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                throw new FileNotFoundException("이미지 파일을 찾을 수 없습니다: " + imageName);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/get-image-path/{id}")
    public ResponseEntity<ImageDto> getImagePath(@PathVariable Long id) {
        try {
            // 서비스 호출로 ImageDto 가져오기
            ImageDto imageDto = imageService.getImageDtoById(id);
            return ResponseEntity.ok(imageDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
