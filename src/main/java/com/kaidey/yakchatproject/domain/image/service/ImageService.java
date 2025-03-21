package com.kaidey.yakchatproject.domain.image.service;

import com.kaidey.yakchatproject.domain.answer.entity.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.kaidey.yakchatproject.domain.image.repository.ImageRepository;
import com.kaidey.yakchatproject.domain.image.entity.Image;
import com.kaidey.yakchatproject.domain.image.dto.ImageDto;

import java.util.List;
import java.io.IOException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.ArrayList;

import java.util.Optional;

@Service
public class ImageService {


    @Value("${upload.dir}")
    private String uploadDir;

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


    public List<Image> saveImages(List<MultipartFile> files, Answer answer) throws IOException {
        List<Image> imageList = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            if (file.isEmpty()) {
                continue;
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + "/" + fileName;
            String mimeType = file.getContentType();

            File saveFile = new File(filePath);
            file.transferTo(saveFile);

            Image image = new Image();
            image.setFileName(fileName);
            image.setUrl(fileName);
            image.setMime(mimeType);
            image.setStepIndex(i); // 프로필 이미지는 StepIndex를 따로 사용하지 않음

            if (answer != null) {
                image.setAnswer(answer);
            }

            imageList.add(image);
        }

        return imageRepository.saveAll(imageList);
    }




}
