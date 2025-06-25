package com.jay.brevity.restcontroller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jay.brevity.model.req.CreateShortUrlRequest;
import com.jay.brevity.model.req.UpdateShortUrlRequest;
import com.jay.brevity.model.resp.ApiResponse;
import com.jay.brevity.model.resp.ShortUrlResponse;
import com.jay.brevity.service.ShortUrlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/short-urls")
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShortUrlResponse>> createShortUrl(@RequestBody CreateShortUrlRequest request) {
        ShortUrlResponse response = shortUrlService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Short URL created successfully", response));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<ApiResponse<ShortUrlResponse>> getShortUrl(@PathVariable String shortCode) {
        ShortUrlResponse response = shortUrlService.getShortUrl(shortCode);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{shortCode}/redirect")
    public ResponseEntity<ApiResponse<String>> getRedirectUrl(@PathVariable String shortCode) {
        String longUrl = shortUrlService.redirectToLongUrl(shortCode);
        shortUrlService.incrementClickCount(shortCode);
        return ResponseEntity.ok(ApiResponse.success(longUrl));
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<ApiResponse<ShortUrlResponse>> updateShortUrl(
            @PathVariable String shortCode,
            @RequestBody UpdateShortUrlRequest request) {
        ShortUrlResponse response = shortUrlService.updateShortUrl(shortCode, request);
        return ResponseEntity.ok(ApiResponse.success("Short URL updated successfully", response));
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<ApiResponse<Void>> deleteShortUrl(@PathVariable String shortCode) {
        shortUrlService.deleteShortUrl(shortCode);
        return ResponseEntity.ok(ApiResponse.success("Short URL deleted successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShortUrlResponse>>> getAllShortUrls() {
        List<ShortUrlResponse> responses = shortUrlService.getAllShortUrls();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ShortUrlResponse>>> getActiveShortUrls() {
        List<ShortUrlResponse> responses = shortUrlService.getActiveShortUrls();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<List<ShortUrlResponse>>> getExpiredShortUrls() {
        List<ShortUrlResponse> responses = shortUrlService.getExpiredShortUrls();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/creator/{createdBy}")
    public ResponseEntity<ApiResponse<List<ShortUrlResponse>>> getShortUrlsByCreator(@PathVariable String createdBy) {
        List<ShortUrlResponse> responses = shortUrlService.getShortUrlsByCreator(createdBy);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<ShortUrlResponse>>> getPopularShortUrls(
            @RequestParam(defaultValue = "10") Long minClicks) {
        List<ShortUrlResponse> responses = shortUrlService.getPopularShortUrls(minClicks);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/check/{shortCode}")
    public ResponseEntity<ApiResponse<Boolean>> checkShortCodeAvailability(@PathVariable String shortCode) {
        boolean isAvailable = shortUrlService.isShortCodeAvailable(shortCode);
        return ResponseEntity.ok(ApiResponse.success(isAvailable));
    }

    @GetMapping("/generate-code")
    public ResponseEntity<ApiResponse<String>> generateShortCode() {
        String shortCode = shortUrlService.generateShortCode();
        return ResponseEntity.ok(ApiResponse.success(shortCode));
    }
}