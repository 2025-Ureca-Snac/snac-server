package com.ureca.snac.auth.util;

import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Getter
@Component
public class PromptLoader {
    private final String prompt;

    public PromptLoader(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("classpath:prompt/imageValidationPrompt.txt");
        try{InputStream inputStream = resource.getInputStream();
            this.prompt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("프롬프트 로딩 실패: imageValidationPrompt.txt", e);
        }
    }
}