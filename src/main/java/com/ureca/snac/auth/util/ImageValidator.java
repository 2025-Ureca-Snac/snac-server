package com.ureca.snac.auth.util;

import com.ureca.snac.auth.exception.lmm.ImageValidationBusinessException;
import com.ureca.snac.common.BaseCode;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

public class ImageValidator {

    public static final Set<String> DEFAULT_ALLOWED_MIME_TYPES = Set.of(
            "image/png", "image/jpeg", "image/jpg", "image/webp"
    );

    private final Set<String> allowedMimeTypes;

    public record PreparedImage(ByteArrayResource resource, String mimeType) {}

    public ImageValidator() {
        this(DEFAULT_ALLOWED_MIME_TYPES);
    }

    public ImageValidator(Set<String> allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    public PreparedImage validateAndPrepare(MultipartFile file) {
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ImageValidationBusinessException(
                    BaseCode.IMAGE_NOT_SUPPORT_TYPE,
                    "이미지 바이트 처리 중 오류가 발생했습니다."
            );
        }

        String detected = detectImageMimeType(bytes);
        if (detected == null || !allowedMimeTypes.contains(detected)) {
            throw new ImageValidationBusinessException(
                    BaseCode.IMAGE_NOT_SUPPORT_TYPE,
                    "지원되지 않는 이미지 형식입니다."
            );
        }

        return new PreparedImage(new ByteArrayResource(bytes), detected);
    }

    private String detectImageMimeType(byte[] bytes) {
        if (bytes.length >= 8 &&
                (bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47 &&
                        bytes[4] == 0x0D && bytes[5] == 0x0A && bytes[6] == 0x1A && bytes[7] == 0x0A)) {
            return "image/png";
        }
        if (bytes.length >= 3 &&
                (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF)) {
            return "image/jpeg";
        }
        if (bytes.length >= 12 &&
                bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F' &&
                bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') {
            return "image/webp";
        }
        return null;
    }
}
