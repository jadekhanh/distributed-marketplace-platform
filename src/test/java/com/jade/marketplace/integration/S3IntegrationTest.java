package com.jade.marketplace.integration;

import com.jade.marketplace.s3.S3Service;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Real AWS S3 integration test
 *
 * Disabled by default because it requires:
 * - real AWS credentials
 * - real S3 bucket
 * - possible AWS costs
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires real AWS S3 credentials and bucket")
class S3IntegrationTest {

    @Autowired
    private S3Service s3Service;

    /**
     * Upload bytes should upload file to AWS S3
     * Uploaded file can be verified by manually checking inside S3 bucket
     */
    @Test
    void uploadBytes_shouldUploadFileToS3() {
        // create S3 object key
        String key = "test/plushies-s3.txt";

        // convert content text into bytes since S3 only uploads bytes
        byte[] content = "Give me all the plushies!".getBytes();

        // upload bytes to S3 using object key, content
        s3Service.uploadBytes(key, content, "text/plain");

        // build public url of the thing we upload
        String url = s3Service.buildObjectUrl(key);

        // check the url contains "amazonaws.com" which is valid AWS URL
        assertTrue(url.contains("amazonaws.com"));

        // check the url points to the file we upload
        assertTrue(url.contains(key));
    }
}