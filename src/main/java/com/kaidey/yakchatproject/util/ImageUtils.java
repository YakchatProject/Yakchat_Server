package com.kaidey.yakchatproject.util;

import com.kaidey.yakchatproject.dto.ImageDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ImageUtils {

    public static List<ImageDto> processBase64Images(String[] base64Images) throws IOException {
        // 반환할 ImageDto 리스트
        List<ImageDto> imageDtos = new ArrayList<>();

        for (String base64Image : base64Images) {
            // null 체크 및 기본 형식 검증
            if (base64Image == null || base64Image.isEmpty()) {
                throw new IllegalArgumentException("Invalid Base64 format: Input is null or empty.");
            }
            if (!base64Image.startsWith("data:image/")) {
                throw new IllegalArgumentException(
                        "Invalid metadata format: Metadata should start with 'data:image/'. Received: " + base64Image);
            }
            if (!base64Image.contains(";base64,")) {
                throw new IllegalArgumentException(
                        "Invalid metadata format: Metadata should contain ';base64,'. Received: " + base64Image);
            }

            // 쉼표로 나누기 (데이터와 메타데이터 분리)
            String[] parts = base64Image.split(",", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException(
                        "Invalid Base64 format: Incorrect number of parts. Expected format 'data:image/<type>;base64,<data>'. Received: " + base64Image);
            }

            String metadata = parts[0];
            String base64Data = parts[1];

            // Base64 데이터 디코딩
            byte[] imageData;
            try {
                imageData = Base64.getDecoder().decode(base64Data);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Failed to decode Base64 data. Ensure the input is correctly encoded.", e);
            }

            // 파일 이름 추출
            String fileName = extractFileNameFromMetadata(metadata);

            // ImageDto 생성 및 리스트에 추가
            ImageDto imageDto = new ImageDto();
            imageDto.setData(imageData);
            imageDto.setFileName(fileName);
            imageDtos.add(imageDto);
        }

        return imageDtos;
    }

    // 메타데이터에서 파일 이름 추출
    private static String extractFileNameFromMetadata(String metadata) {
        // 예: data:image/jpeg;base64
        if (!metadata.contains("/")) {
            throw new IllegalArgumentException("Invalid metadata format: Missing '/' in metadata. Received: " + metadata);
        }
        if (!metadata.contains(";")) {
            throw new IllegalArgumentException("Invalid metadata format: Missing ';' in metadata. Received: " + metadata);
        }

        String fileType = metadata.split("/")[1].split(";")[0]; // 이미지 유형 추출
        return "image." + fileType; // 예: image.jpeg
    }
}
