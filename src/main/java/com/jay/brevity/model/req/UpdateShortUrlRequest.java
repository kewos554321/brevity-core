package com.jay.brevity.model.req;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UpdateShortUrlRequest {

    private String longUrl;

    private LocalDateTime expiresAt;

    private String updatedBy;
}