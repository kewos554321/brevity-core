package com.jay.brevity.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.brevity.model.entity.ShortUrl;
import com.jay.brevity.model.req.CreateShortUrlRequest;
import com.jay.brevity.model.req.UpdateShortUrlRequest;
import com.jay.brevity.model.resp.ShortUrlResponse;
import com.jay.brevity.repository.ShortUrlRepository;
import com.jay.brevity.service.ShortUrlService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 8;
    private final Random random = new Random();

    @Override
    public ShortUrlResponse createShortUrl(CreateShortUrlRequest request) {
        String shortCode = request.getShortCode();
        
        if (shortCode == null || shortCode.trim().isEmpty()) {
            shortCode = generateShortCode();
        } else if (!isShortCodeAvailable(shortCode)) {
            throw new RuntimeException("Short code already exists: " + shortCode);
        }

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortCode(shortCode);
        shortUrl.setLongUrl(request.getLongUrl());
        shortUrl.setExpiresAt(request.getExpiresAt());
        shortUrl.setCreatedBy(request.getCreatedBy());
        shortUrl.setClickCount(0L);

        ShortUrl savedShortUrl = shortUrlRepository.save(shortUrl);
        log.info("Created short URL: {} -> {}", shortCode, request.getLongUrl());
        
        return convertToResponse(savedShortUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public ShortUrlResponse getShortUrl(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode));
        
        return convertToResponse(shortUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public String redirectToLongUrl(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode));
        
        if (shortUrl.isExpired()) {
            throw new RuntimeException("Short URL has expired: " + shortCode);
        }
        
        return shortUrl.getLongUrl();
    }

    @Override
    public ShortUrlResponse updateShortUrl(String shortCode, UpdateShortUrlRequest request) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode));
        
        shortUrl.setLongUrl(request.getLongUrl());
        shortUrl.setExpiresAt(request.getExpiresAt());
        shortUrl.setUpdatedBy(request.getUpdatedBy());
        
        ShortUrl updatedShortUrl = shortUrlRepository.save(shortUrl);
        log.info("Updated short URL: {}", shortCode);
        
        return convertToResponse(updatedShortUrl);
    }

    @Override
    public void deleteShortUrl(String shortCode) {
        if (!shortUrlRepository.existsByShortCode(shortCode)) {
            throw new RuntimeException("Short URL not found: " + shortCode);
        }
        
        shortUrlRepository.deleteById(shortCode);
        log.info("Deleted short URL: {}", shortCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortUrlResponse> getAllShortUrls() {
        return shortUrlRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortUrlResponse> getActiveShortUrls() {
        return shortUrlRepository.findActiveUrls(LocalDateTime.now()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortUrlResponse> getExpiredShortUrls() {
        return shortUrlRepository.findExpiredUrls(LocalDateTime.now()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortUrlResponse> getShortUrlsByCreator(String createdBy) {
        return shortUrlRepository.findByCreatedBy(createdBy).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShortUrlResponse> getPopularShortUrls(Long minClicks) {
        return shortUrlRepository.findPopularUrls(minClicks).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void incrementClickCount(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found: " + shortCode));
        
        shortUrl.incrementClickCount();
        shortUrlRepository.save(shortUrl);
        log.debug("Incremented click count for short URL: {}", shortCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isShortCodeAvailable(String shortCode) {
        return !shortUrlRepository.existsByShortCode(shortCode);
    }

    @Override
    public String generateShortCode() {
        StringBuilder shortCode = new StringBuilder();
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            shortCode.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        
        // Ensure uniqueness
        while (!isShortCodeAvailable(shortCode.toString())) {
            shortCode = new StringBuilder();
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                shortCode.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
        }
        
        return shortCode.toString();
    }

    private ShortUrlResponse convertToResponse(ShortUrl shortUrl) {
        ShortUrlResponse response = new ShortUrlResponse();
        response.setShortCode(shortUrl.getShortCode());
        response.setLongUrl(shortUrl.getLongUrl());
        response.setCreatedAt(shortUrl.getCreatedAt());
        response.setUpdatedAt(shortUrl.getUpdatedAt());
        response.setExpiresAt(shortUrl.getExpiresAt());
        response.setClickCount(shortUrl.getClickCount());
        response.setCreatedBy(shortUrl.getCreatedBy());
        response.setUpdatedBy(shortUrl.getUpdatedBy());
        response.setExpired(shortUrl.isExpired());
        return response;
    }
} 