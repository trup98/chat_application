package com.learning.real_time_chat_application.utill;

import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


public class FileValidationUtils {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "application/pdf", "text/plain"
    );

    public static void validateFile(MultipartFile file) {
        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(
                    ExceptionEnum.FILE_SIZE_EXCEEDED.getMessage(),
                    HttpStatus.PAYLOAD_TOO_LARGE
            );
        }

        // Validate file type
        String fileContentType = file.getContentType();
        if (!ALLOWED_FILE_TYPES.contains(fileContentType)) {
            throw new CustomException(
                    ExceptionEnum.INVALID_FILE_TYPE.getMessage(),
                    HttpStatus.NOT_ACCEPTABLE
            );
        }
    }
}