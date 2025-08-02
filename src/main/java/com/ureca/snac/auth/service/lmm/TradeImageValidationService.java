package com.ureca.snac.auth.service.lmm;

import com.fasterxml.jackson.databind.JsonNode;
import com.ureca.snac.auth.dto.response.ImageValidationResult;
import com.ureca.snac.auth.exception.lmm.ImageValidationBusinessException;
import com.ureca.snac.auth.exception.lmm.ImageValidationLlmException;
import com.ureca.snac.auth.util.ImageValidator;
import com.ureca.snac.auth.util.PromptLoader;
import com.ureca.snac.common.BaseCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradeImageValidationService {

    private final PromptLoader promptLoader;
    private final ImageValidator imageValidator;
    private final LlmImageValidationClient llmClient;

    public ImageValidationResult validateImage(MultipartFile file) {
        String filename = file.getOriginalFilename();
        long size = file.getSize();
        log.info("[validateImage] 시작 - 파일명 = < {} >, 파일 크기 = < {} 바이트 >", filename, size);

        try {
            ImageValidator.PreparedImage prepared = imageValidator.validateAndPrepare(file);
            String promptText = promptLoader.getPrompt();

            JsonNode root = llmClient.validateWithLlm(promptText, prepared, filename);
            ImageValidationResult result = buildResult(root);
            log.info("[validateImage] 종료 - 파일명 = < {} >, 검증됐나요? = {}, message={}",
                    filename, result.valid(), result.message());
            return result;
        } catch (ImageValidationLlmException | ImageValidationBusinessException e) {
            throw e;
        } catch (Exception unexpected) {
            log.error("[validateImage] 예기치 않은 오류 - 파일명 = < {} >", filename, unexpected);
            throw new ImageValidationBusinessException(
                    BaseCode.IMAGE_VALIDATION_INVALID_RESULT,
                    "이미지 검증 중 내부 오류가 발생했습니다."
            );
        }
    }

    private ImageValidationResult buildResult(JsonNode root) {
        boolean valid = root.path("valid").asBoolean(false);
        String message = "";
        if (!valid && root.has("message")) {
            String m = root.path("message").asText().trim();
            if (!m.isEmpty()) {
                message = m;
            }
        }
        return new ImageValidationResult(valid, message);
    }
}
