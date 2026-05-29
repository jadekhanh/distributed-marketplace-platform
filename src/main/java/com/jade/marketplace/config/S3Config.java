package com.jade.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3 configuration to create AWS S3 client to be used by application
 * S3 stores product images and other uploaded marketplace files
 */
@Configuration
public class S3Config {

    // reads AWS region from application.yml
    @Value("${aws.region}")
    private String awsRegion;

    // creates an S3 client bean that other services can inject and use
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .build();
    }
}