package com.kaidey.yakchatproject.service;

import org.springframework.stereotype.Service;
import com.kaidey.yakchatproject.repository.ImageRepository;
import com.kaidey.yakchatproject.entity.Image;
import com.kaidey.yakchatproject.dto.ImageDto;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final String uploadDir = "uploads/";

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


    public Map<String, String> saveImages(List<MultipartFile> files, Long questionId, Long answerId, Long userId) throws IOException {
        Map<String, String> imageMap = new LinkedHashMap<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + fileName;
            String mimeType = file.getContentType();

            File saveFile = new File(filePath);
            file.transferTo(saveFile);

            Image image = new Image();
            image.setFileName(fileName);
            image.setUrl(filePath);
            image.setMime(mimeType);


            imageRepository.save(image);
            imageMap.put("image_" + (i + 1), filePath);
        }

        return imageMap;
    }
}
