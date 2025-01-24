package com.kaidey.yakchatproject.util;

import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Component
public class ImageUtils {

    @Value("${upload.dir}")
    private String uploadDir;


    public String saveBase64Image(String base64, String fileName) throws MimeTypeException {
        System.out.println("Base64 Input Length: " + base64.length());

        int colon = base64.indexOf(":");
        int semicolon = base64.indexOf(";");
        String mimeType = base64.substring(colon + 1, semicolon);

        System.out.println("MIME Type: " + mimeType);

        String base64WithoutHeader = base64.substring(semicolon + 8);
        System.out.println("Base64 Data Length: " + base64WithoutHeader.length());

        // MIME 타입 매핑 및 확장자 추출
        String extension;
        try {
            extension = MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
            System.out.println("File Extension: " + extension);
        } catch (MimeTypeException e) {
            System.out.println("MimeTypeException: " + e.getMessage());
            throw e;
        }

        // Base64 디코딩
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(base64WithoutHeader);
            System.out.println("Decoded Bytes Length: " + bytes.length);
        } catch (IllegalArgumentException e) {
            System.out.println("Base64 decoding failed: " + e.getMessage());
            throw e;
        }

        // 파일 저장 경로 설정
        fileName = fileName + "_" + UUID.randomUUID() + extension;
        File file = new File(uploadDir + "/" + fileName);

        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("Directories created successfully.");
            } else {
                System.out.println("Failed to create directories.");
            }
        }

        // 파일 쓰기
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(bytes);
            System.out.println("File saved successfully at: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to write file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save image file", e);
        }

        return "/images/" + fileName;
    }

}
