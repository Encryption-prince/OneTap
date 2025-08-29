package com.coding.OneTap.service;

import com.coding.OneTap.model.FileMetadata;
import com.coding.OneTap.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository metadataRepository;
    private final RedisTemplate<String, byte[]> redisTemplate;

    public UUID uploadFile(MultipartFile file, int expiryMinutes) throws Exception {
        // Generate unique Redis key
        String redisKey = "file:" + UUID.randomUUID();

        // Store file bytes in Redis with TTL
        redisTemplate.opsForValue().set(redisKey, file.getBytes(), expiryMinutes, TimeUnit.MINUTES);

        // Save metadata in Postgres
        FileMetadata metadata = FileMetadata.builder()
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .uploadTime(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusMinutes(expiryMinutes))
                .redisKey(redisKey)
                .build();

        FileMetadata saved = metadataRepository.save(metadata);

        return saved.getId();
    }

    public byte[] getFileOnce(UUID fileId) throws Exception {
        FileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found or expired"));

        byte[] fileBytes = redisTemplate.opsForValue().get(metadata.getRedisKey());
        if (fileBytes == null) {
            // Redis already expired or file retrieved before
            metadataRepository.deleteById(fileId);
            throw new Exception("File expired or already viewed");
        }

        // Delete from Redis and Postgres after retrieval
        redisTemplate.delete(metadata.getRedisKey());
        metadataRepository.deleteById(fileId);

        return fileBytes;
    }

}