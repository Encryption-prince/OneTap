package com.coding.OneTap.controller;

import com.coding.OneTap.dto.FileResponse;
import com.coding.OneTap.dto.FileWithExpiry;
import com.coding.OneTap.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam(defaultValue = "10") int expiryMinutes) {
        try {
            UUID fileId = fileService.uploadFile(file, expiryMinutes);
            String link = "http://localhost:8080/api/files/view/" + fileId;
            return ResponseEntity.ok(link);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }

    @GetMapping("/view/{fileId}")
    public ResponseEntity<?> viewFile(@PathVariable UUID fileId) {
        try {
            FileWithExpiry fileData = fileService.getFileOnce(fileId);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"file.bin\"")
                    .header("Content-Type", "application/octet-stream")
                    .header("X-Expiry-Seconds", String.valueOf(fileData.getExpirySeconds()))
                    .body(fileData.getFileBytes());

        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


//    @GetMapping("/view/{fileId}")
//    public ResponseEntity<?> viewFile(@PathVariable UUID fileId) {
//        try {
//            byte[] fileBytes = fileService.getFileOnce(fileId);
////            FileResponse response = fileService.getFileOnceWithExpiry(fileId);
//
//            // We’ll return as application/octet-stream since content type is gone after retrieval
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", "inline; filename=\"file.bin\"")
//                    .header("Content-Type", "application/octet-stream")
////                    .header("Content-Type", "application/json")
////                    .body(response);
//                    .body(fileBytes);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(404).body(e.getMessage());
//        }
//    }

}
