package com.ureca.snac.auth.service.lmm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.auth.dto.response.ImageValidationResult;
import com.ureca.snac.auth.exception.lmm.ImageValidationBusinessException;
import com.ureca.snac.auth.exception.lmm.ImageValidationLlmException;
import com.ureca.snac.auth.exception.lmm.ImageValidationResponseParseException;
import com.ureca.snac.auth.util.PromptLoader;
import com.ureca.snac.common.BaseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.azure.openai.AzureOpenAiResponseFormat;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class TradeImageValidationService {

    private static final String DEFAULT_DEPLOYMENT_NAME = "gpt-4o";

    private final AzureOpenAiChatModel chatModel;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PromptLoader promptLoader;

    public TradeImageValidationService(AzureOpenAiChatModel chatModel, PromptLoader promptLoader) {
        this.chatModel = chatModel;
        this.promptLoader = promptLoader;
    }

    public ImageValidationResult validateImage(MultipartFile file) {
        String filename = file.getOriginalFilename();
        long size = file.getSize();
        log.info("[validateImage] 시작 - 파일명 = < {} >, 파일 크기 = < {} 바이트 >", filename, size);

        try {
            ByteArrayResource imageResource = toResource(file, filename);
            JsonNode root = callLlmAndParse(imageResource, filename);
            ImageValidationResult result = buildResult(root);
            log.info("[validateImage] 종료 - 파일명 = < {} >, 검증됐나요? = {}, message={}",
                    filename, result.valid(), result.message());
            return result;
        } catch (ImageValidationLlmException | ImageValidationResponseParseException | ImageValidationBusinessException e) {
            throw e;
        } catch (Exception unexpected) {
            log.error("[validateImage] 예기치 않은 오류 - 파일명 = < {} >", filename, unexpected);
            throw new ImageValidationBusinessException(
                    BaseCode.IMAGE_VALIDATION_INVALID_RESULT,
                    "이미지 검증 중 내부 오류가 발생했습니다."
            );
        }
    }

    private ByteArrayResource toResource(MultipartFile file, String filename) {
        try {
            return new ByteArrayResource(file.getBytes());
        } catch (IOException e) {
            log.error("[toResource] 이미지 바이트 읽기 실패 - 파일명 = < {} >", filename, e);
            throw new ImageValidationBusinessException(
                    BaseCode.IMAGE_VALIDATION_INVALID_RESULT,
                    "이미지 처리 중 오류가 발생했습니다."
            );
        }
    }

    private JsonNode callLlmAndParse(ByteArrayResource imageResource, String filename) {
        String promptText = promptLoader.getPrompt();
        var options = AzureOpenAiChatOptions.builder()
                .deploymentName(DEFAULT_DEPLOYMENT_NAME)
                .responseFormat(
                        AzureOpenAiResponseFormat.builder()
                                .type(AzureOpenAiResponseFormat.Type.JSON_OBJECT)
                                .build()
                )
                .build();

        String jsonContent;
        try {
            jsonContent = ChatClient.create(chatModel)
                    .prompt()
                    .options(options)
                    .user(u -> u.text(promptText).media(MimeTypeUtils.IMAGE_PNG, imageResource))
                    .call()
                    .content();
        } catch (Exception callEx) {
            log.error("[callLlmAndParse] LLM 호출 실패 - 파일명 = < {} >", filename, callEx);
            throw new ImageValidationLlmException(
                    BaseCode.IMAGE_VALIDATION_LLM_ERROR,
                    "LLM 호출 실패",
                    callEx
            );
        }

        return parseAndValidateSchema(jsonContent, filename);
    }

    private JsonNode parseAndValidateSchema(String content, String filename) {
        JsonNode root;
        try {
            root = objectMapper.readTree(content);
        } catch (Exception parseEx) {
            log.error("[parseAndValidateSchema] 응답 JSON 파싱 실패 - 파일명 = < {} >", filename, parseEx);
            throw new ImageValidationResponseParseException(
                    BaseCode.IMAGE_VALIDATION_RESPONSE_PARSE_ERROR,
                    "응답 JSON 파싱 실패",
                    parseEx
            );
        }

        if (!root.has("valid")) {
            log.warn("[parseAndValidateSchema] 'valid' 필드 누락 - 파일명 = < {} >", filename);
            throw new ImageValidationResponseParseException(
                    BaseCode.IMAGE_VALIDATION_INVALID_RESULT,
                    "'valid' 필드 없음"
            );
        }

        return root;
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
