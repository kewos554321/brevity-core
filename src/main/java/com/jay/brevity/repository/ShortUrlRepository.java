package com.jay.brevity.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jay.brevity.model.entity.ShortUrl;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, String> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    @Query("SELECT s FROM ShortUrl s WHERE s.expiresAt IS NULL OR s.expiresAt > :now")
    List<ShortUrl> findActiveUrls(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM ShortUrl s WHERE s.expiresAt IS NOT NULL AND s.expiresAt <= :now")
    List<ShortUrl> findExpiredUrls(@Param("now") LocalDateTime now);

    boolean existsByShortCode(String shortCode);

    List<ShortUrl> findByCreatedBy(String createdBy);

    @Query("SELECT s FROM ShortUrl s WHERE s.clickCount > :minClicks ORDER BY s.clickCount DESC")
    List<ShortUrl> findPopularUrls(@Param("minClicks") Long minClicks);
} 