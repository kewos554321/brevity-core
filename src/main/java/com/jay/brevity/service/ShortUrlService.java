package com.jay.brevity.service;

import java.util.List;

import com.jay.brevity.model.req.CreateShortUrlRequest;
import com.jay.brevity.model.req.UpdateShortUrlRequest;
import com.jay.brevity.model.resp.ShortUrlResponse;

public interface ShortUrlService {

    ShortUrlResponse createShortUrl(CreateShortUrlRequest request);

    ShortUrlResponse getShortUrl(String shortCode);

    String redirectToLongUrl(String shortCode);

    ShortUrlResponse updateShortUrl(String shortCode, UpdateShortUrlRequest request);

    void deleteShortUrl(String shortCode);

    List<ShortUrlResponse> getAllShortUrls();

    List<ShortUrlResponse> getActiveShortUrls();

    List<ShortUrlResponse> getExpiredShortUrls();

    List<ShortUrlResponse> getShortUrlsByCreator(String createdBy);

    List<ShortUrlResponse> getPopularShortUrls(Long minClicks);

    void incrementClickCount(String shortCode);

    boolean isShortCodeAvailable(String shortCode);

    String generateShortCode();
} 