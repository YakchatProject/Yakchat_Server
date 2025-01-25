package com.kaidey.yakchatproject.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathToUrlConverter {

    private static final String BASE_URL = "https://endlessly-cuddly-salmon.ngrok-free.app/images/";

    public static String convert(String filePath) {
        Path path = Paths.get(filePath);
        return BASE_URL + path.getFileName().toString();
    }
}