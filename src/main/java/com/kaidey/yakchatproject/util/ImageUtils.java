package com.kaidey.yakchatproject.util;

import com.kaidey.yakchatproject.dto.ImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static List<ImageDto> processImages(MultipartFile[] images) throws IOException {
        List<ImageDto> imageDtos = new ArrayList<>();
        for (MultipartFile image : images) {
            ImageDto imageDto = new ImageDto();
            imageDto.setData(image.getBytes());
            imageDto.setFileName(image.getOriginalFilename());
            imageDtos.add(imageDto);
        }
        return imageDtos;
    }
}