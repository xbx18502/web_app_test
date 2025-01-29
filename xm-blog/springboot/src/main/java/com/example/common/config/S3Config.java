package com.example.common.config;


import org.springframework.util.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;

@Configuration
public class S3Config {
    @Value("${aws.s3.region}")
    private String region;
    
    @Value("${aws.access.key.id:}")  // 冒号后为空表示可选
    private String accessKeyId;
    
    @Value("${aws.secret.access.key:}")
    private String secretKey;
    
    @Bean
    public AmazonS3 s3Client() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withRegion(region);
                
        // 如果提供了访问密钥（本地开发），就使用它们
        if (StringUtils.hasLength(accessKeyId) && StringUtils.hasLength(secretKey)) {
            AWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretKey);
            builder.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        // 否则（在EC2上）使用默认凭证链，会自动使用 IAM Role
        
        return builder.build();
    }
}