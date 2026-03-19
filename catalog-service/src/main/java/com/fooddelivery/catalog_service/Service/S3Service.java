package com.fooddelivery.catalog_service.Service;

import com.fooddelivery.catalog_service.Config.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Properties s3Properties;
    private final S3Client s3Client;

    public String upload(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String key = folder + "/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()
            ));

            String url = s3Properties.getBaseUrl() + "/" + key;
            log.info("Файл загружен в S3: {}", url);
            return url;

        }catch (Exception e) {
            log.error("Ошибка загрузки файла в S3: {}", e.getMessage());
            throw new RuntimeException("Не удалось загрузить файл", e);
        }
    }

    private String getExtension(String filename) {
        return null;
    }
}
