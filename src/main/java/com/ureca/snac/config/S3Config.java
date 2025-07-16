package com.ureca.snac.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(S3Properties.class)
public class S3Config {

    private final S3Properties props; // S3 관련 속성들을 주입

    @Bean
    public S3Client s3Client() { // S3Client 객체를 Spring Bean 으로 등록
        return S3Client.builder()
                .credentialsProvider( // AWS 자격 증명 (Access Key, Secret Key)
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .region(Region.of(props.getRegion()))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(S3Properties props) { // S3Presigner 객체를 Spring Bean 으로 등록
        return S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
                .build();
    }
}