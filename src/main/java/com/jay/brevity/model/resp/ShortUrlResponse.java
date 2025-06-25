package com.jay.brevity.model.resp;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ShortUrlResponse {
    private String shortCode;
    private String longUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private Long clickCount;
    private String createdBy;
    private String updatedBy;
    private boolean expired;
}