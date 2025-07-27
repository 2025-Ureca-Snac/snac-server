package com.ureca.snac.common.s3;


// S3 prefix 상수로 관리 ( 경로처럼 보이는 객체키 접두사 )
// s3Uploader.upload(dto.getImage(), S3Path.TRADE_ATTACHMENT); 이런식으로 사용 ..AttachmentServiceImpl.java
public class S3Path {
    public static final String TRADE_ATTACHMENT = "trade/attachments"; // 거래 첨부 이미지
    public static final String USER_PROFILE = "users/profile"; // 예) 프로필 이미지
    public static final String ARTICLE_CONTENT = "article/content"; // 예) 게시판 이미지
}
