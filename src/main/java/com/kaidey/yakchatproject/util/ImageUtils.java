package com.kaidey.yakchatproject.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtils {

    public static String saveBase64Image(String base64Image, String uploadDir) throws IOException {
        if (base64Image == null || !base64Image.contains(",")) {
            throw new IllegalArgumentException("Invalid Base64 format: Missing comma separating data and metadata.");
        }

        String[] parts = base64Image.split(",", 2);
        String metadata = parts[0];
        String base64Data = parts[1];

        if (!metadata.startsWith("data:image/") || !metadata.contains(";base64")) {
            throw new IllegalArgumentException("Invalid metadata format: Metadata should start with 'data:image/' and contain ';base64'.");
        }

        byte[] imageData = Base64.getDecoder().decode(base64Data);
        String fileName = extractFileNameFromMetadata(metadata);
        File dir = new File(uploadDir);

        // 디렉토리가 존재하지 않으면 생성
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create directory: " + uploadDir);
            }
        }

        File file = new File(dir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageData);
        }

        return file.getAbsolutePath();
    }

    private static String extractFileNameFromMetadata(String metadata) {
        String fileType = metadata.split("/")[1].split(";")[0];
        return "image_" + System.currentTimeMillis() + "." + fileType;
    }
}
