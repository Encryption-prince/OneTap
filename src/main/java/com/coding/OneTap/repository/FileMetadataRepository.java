package com.coding.OneTap.repository;

import com.coding.OneTap.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
}
