package com.ureca.snac.auth.service.lmm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.auth.exception.lmm.ImageValidationLlmException;
import com.ureca.snac.auth.exception.lmm.ImageValidationResponseParseException;
import com.ureca.snac.auth.util.ImageValidator;
import com.ureca.snac.common.BaseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.azure.openai.AzureOpenAiResponseFormat;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.MimeTypeUtils;

import java.util.concurrent.*;

@Slf4j
@RequiredArgsConstructor
public class LlmImageValidationClient {

    private final AzureOpenAiChatModel chatModel;
    private final String deploymentName;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long TIMEOUT_SECONDS = 10;

    public JsonNode validateWithLlm(String promptText, ImageValidator.PreparedImage image, String filename) {
        var options = AzureOpenAiChatOptions.builder()
                .deploymentName(deploymentName)
                .responseFormat(
                        AzureOpenAiResponseFormat.builder()
                                .type(AzureOpenAiResponseFormat.Type.JSON_OBJECT)
                                .build()
                )
                .build();

        String jsonContent;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> ChatClient.create(chatModel)
                .prompt()
                .options(options)
                .user(u -> u.text(promptText)
                        .media(MimeTypeUtils.parseMimeType(image.mimeType()), image.resource()))
                .call()
                .content());

        try {
            jsonContent = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException te) {
            future.cancel(true);
            log.error("[LLM] 호출 타임아웃 ({}초 초과) - 파일명 = < {} >", TIMEOUT_SECONDS, filename, te);
            throw new ImageValidationLlmException(
                    BaseCode.IMAGE_VALIDATION_LLM_ERROR_TIMEOUT,
                    "LLM 호출 타임아웃 (" + TIMEOUT_SECONDS + "초 초과)",
                    te
            );
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("[LLM] 호출 중 인터럽트 발생 - 파일명 = < {} >", filename, ie);
            throw new ImageValidationLlmException(
                    BaseCode.IMAGE_VALIDATION_LLM_ERROR,
                    "LLM 호출이 인터럽트되었습니다.",
                    ie
            );
        } catch (ExecutionException ee) {
            log.error("[LLM] 호출 실패 - 파일명 = < {} >", filename, ee.getCause());
            throw new ImageValidationLlmException(
                    BaseCode.IMAGE_VALIDATION_LLM_ERROR,
                    "LLM 호출 실패",
                    ee.getCause()
            );
        } finally {
            executor.shutdownNow();
        }

        return parseAndValidateSchema(jsonContent, filename);
    }

    private JsonNode parseAndValidateSchema(String content, String filename) {
        JsonNode root;
        try {
            root = objectMapper.readTree(content);
        } catch (Exception parseEx) {
            log.error("[LLM] 응답 JSON 파싱 실패 - 파일명 = < {} >", filename, parseEx);
            throw new ImageValidationResponseParseException(
                    BaseCode.IMAGE_VALIDATION_RESPONSE_PARSE_ERROR,
                    "응답 JSON 파싱 실패",
                    parseEx
            );
        }

        if (!root.has("valid")) {
            log.warn("[LLM] 'valid' 필드 누락 - 파일명 = < {} >", filename);
            throw new ImageValidationResponseParseException(
                    BaseCode.IMAGE_VALIDATION_INVALID_RESULT,
                    "'valid' 필드 없음"
            );
        }

        return root;
    }
}
