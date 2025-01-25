package com.kaidey.yakchatproject.service;

import org.springframework.stereotype.Service;
import com.kaidey.yakchatproject.repository.ImageRepository;
import com.kaidey.yakchatproject.entity.Image;
import com.kaidey.yakchatproject.dto.ImageDto;

import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public ImageDto getImageDtoById(Long id) {
        Optional<Image> optionalImage = imageRepository.findById(id);

        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();

            // ImageDto로 변환
            ImageDto imageDto = new ImageDto();
            imageDto.setId(image.getId());
            imageDto.setFileName(image.getFileName());
            imageDto.setMime(image.getMime());
            imageDto.setUrl("/images/" + image.getFileName());
            return imageDto;
        } else {
            throw new RuntimeException("이미지를 찾을 수 없습니다. ID: " + id);
        }
    }
}
