package com.coding.OneTap.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalFileName;
    private String contentType;
    private long size;

    private LocalDateTime uploadTime;
    private LocalDateTime expiryTime;

    // We will store the Redis key for retrieval
    private String redisKey;
}