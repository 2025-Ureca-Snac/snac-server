package com.ureca.snac.config;

import com.ureca.snac.auth.service.lmm.LlmImageValidationClient;
import com.ureca.snac.auth.util.ImageValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;

@Configuration
public class ImageValidationConfig {

    private static final String DEFAULT_DEPLOYMENT_NAME = "gpt-4o";

    @Bean
    public ImageValidator imageValidator() {
        return new ImageValidator();
    }

    @Bean
    public LlmImageValidationClient llmImageValidationClient(AzureOpenAiChatModel chatModel) {
        return new LlmImageValidationClient(chatModel, DEFAULT_DEPLOYMENT_NAME);
    }
}
