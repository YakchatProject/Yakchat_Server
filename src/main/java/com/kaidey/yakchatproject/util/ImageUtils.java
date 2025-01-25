package com.kaidey.yakchatproject.util;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.kaidey.yakchatproject.dto.ImageDto;
import java.io.*;
import java.util.Base64;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


@Component
public class ImageUtils {

    private final Tika tika = new Tika();

    @Value("${upload.dir}")
    private String uploadDir;

    public List<ImageDto> processImages(List<String> base64Images) throws MimeTypeException {
//        System.out.println("base64Images: " + base64Images);
        List<ImageDto> imageDtos = new ArrayList<>();
        String mime="";
        for (String base64Image : base64Images) {

            String[] parts = base64Image.split(",");


            if (!parts[0].contains(";")) {
                String base64Data = parts[0]; // 전체 데이터를 Base64로 처리
                System.out.println("Base64 Data without prefix: " + base64Data);


                // 파일 이름 설정
                String fileName = "image_" + UUID.randomUUID();

                // base64 데이터를 저장하고 URL 반환
                String imageUrl = saveBase64Image(base64Data, fileName, mime);


                // ImageDto 객체 생성하여 URL과 파일 이름 설정
                ImageDto imageDto = new ImageDto();
                imageDto.setFileName(fileName);
                imageDto.setMime(mime);

                // 결과 리스트에 추가
                imageDtos.add(imageDto);
            }
            else if (parts.length == 2) {
                // base64 데이터는 parts[1]
                String base64Data = parts[1];
                System.out.println("Base64 Data: " + base64Data);


                if (base64Data.startsWith("data:image/")) {
                    mime = base64Data.substring(5, base64Data.indexOf(";"));
                    System.out.println("Extracted MIME Type: " + mime);
                }


                // base64 데이터를 저장하고 URL 반환
                String fileName = "image_" + UUID.randomUUID();
                String imageUrl = saveBase64Image(base64Data, fileName, mime );
                ImageDto imageDto = new ImageDto();
                imageDto.setFileName(fileName);
                imageDto.setMime(mime);
                imageDtos.add(imageDto);
            }
            else if (parts[0].contains("data:image")) {
                mime = parts[0].substring(5, parts[0].indexOf(";")); // "data:" 이후부터 ";" 전까지
                System.out.println("MIME Type: " + mime);


            }
            else {
                System.out.println("Invalid base64 image format: " + base64Image);

                // 잘못된 데이터 처리 (필요 시 예외 처리)
            }


        }
        System.out.println("MIME Type: " + mime);
        return imageDtos;
    }

    public String saveBase64Image(String base64Data, String fileName, String mime) {
        base64Data = base64Data.trim();

        System.out.println("mimeType Data: " + mime);

        // 고정된 MIME 타입과 확장자 사용
        String fileExtension = mime.split("/")[1];  // 확장자 추출 (예: "png")
        fileName = fileName + "_" + UUID.randomUUID().toString() + "." + fileExtension;
        File file = new File(uploadDir + "/" + fileName);

        // 디렉토리 생성 (없는 경우)
        File parentDir = file.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new RuntimeException("Failed to create directories.");
        }

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
            outputStream.write(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image file", e);
        }

        return FilePathToUrlConverter.convert(file.getAbsolutePath());
    }
}
