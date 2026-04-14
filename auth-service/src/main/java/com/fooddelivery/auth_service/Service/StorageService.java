package com.fooddelivery.auth_service.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final S3Client s3Client;

    @Value("${storage.bucket}")
    private String bucket;

    @Value("${storage.base-url}")
    private String baseUrl;

    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return baseUrl + "/" + fileName;

        } catch (Exception e) {
            log.error("Ошибка загрузки файла: {}", e.getMessage());
            throw new RuntimeException("Не удалось загрузить файл");
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String key = fileUrl.replace(baseUrl + "/", "");

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
        } catch (Exception e) {
            log.error("Ошибка удаления файла: {}", e.getMessage());
        }
    }
}