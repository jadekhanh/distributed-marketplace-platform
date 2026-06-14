package com.jade.marketplace.s3;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * S3 Service to handle AWS S3 logic
 * 
 * In this project: we store URL in MySQL and image files are stored in S3
 * 
 * Reposibilities:
 * Build image keys
 * Upload files to S3
 * Generate image URLs
 */
@Service
public class S3Service {

    // Client to talk to S3
    // Similar to what RedisTemplate to Redis and JpaRepository to MySQL
    private final S3Client s3client;

    /**
     * S3 bucket name which is read from application.yml
     * 
     * Example: aws.bucket=marketplace-dev
     */
    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * AWS region which is read from application.yml
     * Example: aws.region=use-east-2
     */
    @Value("${aws.s3.region}")
    private String region;

    /**
     * Constructor
     */
    public S3Service(S3Client s3client) {
        this.s3client = s3client;
    }

    /**
     * Uploads image bytes to S3 under a given object key
     * Example: 
     * object key = products/1/image-milu-day-1.png
     * fileBytes = imageBytes
     * contentType = "image/png" or "image/jpeg"
     */
    public void uploadBytes(String key, byte[] fileBytes, String contentType) {
        // build request using S3 bucket name, object key, and content type
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(key).contentType(contentType).build();

        // send data to AWS
        s3client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromBytes(fileBytes));
    }

    /**
     * Builds a standard S3 image URL
     * Example: convert "products/1/image-milu-day-1.png" into "https://marketplace-dev.s3.us-east-2.amazonaws.com/products/1/milu.png"
     */
    public String buildObjectUrl(String key) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
    }

    /**
     * Builds a unique product image key/file name
     * Example: original file name = "Milu Day 1.png"
     */
    public String buildProductImageKey(Long productId, String originalFileName) {
        String safeFileName;
        // if file name is null, use "image"
        if (originalFileName == null) {
            safeFileName = "image";
        }
        // if file name exists, replace all whitespace with -
        else {
            safeFileName = originalFileName.replaceAll("\\s+", "-");
        }

        // return image key using product ID, current timestamp + generated safe file name
        return "products/" + productId + "/" + System.currentTimeMillis() + "-" + safeFileName;
    }
}
