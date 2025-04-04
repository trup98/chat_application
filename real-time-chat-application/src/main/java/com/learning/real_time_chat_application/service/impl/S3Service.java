package com.learning.real_time_chat_application.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
@Slf4j
public class S3Service {

    private final Region region = Region.AP_SOUTH_1;

    private final S3Client s3Client = S3Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

    private final S3Presigner preSigner = S3Presigner.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

    /**
     * Generate a Pre-signed URL for accessing private S3 objects
     */
    public String generatePreSignedUrl(String objectKey) {
        String bucketName = "chat-application-learning";
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))  // URL valid for 60 minutes
                .getObjectRequest(getObjectRequest)
                .build();

        return preSigner.presignGetObject(preSignRequest).url().toString();
    }

    /*
     * Delete File from s3
     * */
    public void deleteFileFromS3(String objectKey) {
        String bucketName = "chat-application-learning";
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        log.info("Deleted file from S3 : {}", deleteObjectRequest);
    }
}
