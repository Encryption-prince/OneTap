package com.coding.OneTap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileWithExpiry {
    private byte[] fileBytes;
    private long expirySeconds;
}
