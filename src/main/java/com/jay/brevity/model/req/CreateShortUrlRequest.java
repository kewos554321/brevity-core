package com.jay.brevity.model.req;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateShortUrlRequest {

    private String longUrl;

    private String shortCode; // Optional, will be auto-generated if not provided

    private LocalDateTime expiresAt;

    private String createdBy;
}