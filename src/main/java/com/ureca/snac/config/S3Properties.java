package com.ureca.snac.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.s3")  // aws.s3 접두사로 시작하는 속성들을 이 클래스 필드에 바인딩
public class S3Properties {
    private String region;
    private String bucket;
    private String accessKey;
    private String secretKey;
    private String attachmentDir;
}