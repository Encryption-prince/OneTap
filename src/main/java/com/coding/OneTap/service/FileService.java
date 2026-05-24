package com.coding.OneTap.service;

import com.coding.OneTap.dto.FileResponse;
import com.coding.OneTap.dto.FileWithExpiry;
import com.coding.OneTap.model.FileMetadata;
import com.coding.OneTap.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository metadataRepository;
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final DocumentConversionService conversionService;

    public UUID uploadFile(MultipartFile file, int expiryMinutes) throws Exception {
        byte[] fileBytes = file.getBytes();
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        // Convert DOCX / PPTX / XLSX → PDF before storing
        if (conversionService.shouldConvert(fileName, contentType)) {
            fileBytes = conversionService.convertToPdf(fileBytes, fileName);
            fileName = conversionService.toPdfFilename(fileName);
            contentType = "application/pdf";
        }

        // Generate unique Redis key
        String redisKey = "file:" + UUID.randomUUID();

        // Store file bytes in Redis with TTL
        redisTemplate.opsForValue().set(redisKey, fileBytes, expiryMinutes, TimeUnit.MINUTES);

        // Save metadata in Postgres
        FileMetadata metadata = FileMetadata.builder()
                .originalFileName(fileName)
                .contentType(contentType)
                .size(fileBytes.length)
                .uploadTime(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusMinutes(expiryMinutes))
                .redisKey(redisKey)
                .build();

        FileMetadata saved = metadataRepository.save(metadata);

        return saved.getId();
    }

    public FileWithExpiry getFileOnce(UUID fileId) throws Exception {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found or expired"));

        String redisKey = metadata.getRedisKey();

        byte[] fileBytes = redisTemplate.opsForValue().get(redisKey);
        if (fileBytes == null) {
            metadataRepository.deleteById(fileId);
            throw new Exception("File expired or already viewed");
        }

        // get remaining TTL
        Long expirySeconds = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        if (expirySeconds == null) expirySeconds = 0L;

        // delete file after retrieval (one-time)
        redisTemplate.delete(redisKey);
        metadataRepository.deleteById(fileId);

        return new FileWithExpiry(fileBytes, expirySeconds);
    }

//    public byte[] getFileOnce(UUID fileId) throws Exception {
//        FileMetadata metadata = metadataRepository.findById(fileId)
//                .orElseThrow(() -> new Exception("File not found or expired"));
//
//        byte[] fileBytes = redisTemplate.opsForValue().get(metadata.getRedisKey());
//        if (fileBytes == null) {
//            // Redis already expired or file retrieved before
//            metadataRepository.deleteById(fileId);
//            throw new Exception("File expired or already viewed");
//        }
//
//        // Delete from Redis and Postgres after retrieval
//        redisTemplate.delete(metadata.getRedisKey());
//        metadataRepository.deleteById(fileId);
//
//        return fileBytes;
//    }

public FileResponse getFileOnceWithExpiry(UUID fileId) throws Exception {
    FileMetadata metadata = metadataRepository.findById(fileId)
            .orElseThrow(() -> new Exception("File not found or expired"));

    String redisKey = metadata.getRedisKey();

    // Fetch file bytes from Redis
    byte[] fileBytes = redisTemplate.opsForValue().get(redisKey);
    if (fileBytes == null) {
        metadataRepository.deleteById(fileId);
        throw new Exception("File expired or already viewed");
    }

    // Get remaining TTL in seconds before deletion
    Long expirySeconds = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
    if (expirySeconds == null) expirySeconds = 0L;

    // Convert file to Base64 for JSON transport
    String base64Data = Base64.getEncoder().encodeToString(fileBytes);

    // Delete file after retrieval
    redisTemplate.delete(redisKey);
    metadataRepository.deleteById(fileId);

    return new FileResponse(base64Data, expirySeconds);
}

}