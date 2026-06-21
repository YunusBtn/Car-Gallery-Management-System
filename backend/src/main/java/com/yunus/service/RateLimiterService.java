package com.yunus.service;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    @Value("${rate-limit.auth.capacity:10}")
    private int authLimit;

    @Value("${rate-limit.general.capacity:100}")
    private int generalLimit;

    public boolean tryConsume(String ip, String path) {
        if (path.startsWith("/api/auth")) {
            Bucket bucket = authBuckets.computeIfAbsent(ip, this::newAuthBucket);
            return bucket.tryConsume(1);
        } else {
            Bucket bucket = generalBuckets.computeIfAbsent(ip, this::newGeneralBucket);
            return bucket.tryConsume(1);
        }
    }

    private Bucket newAuthBucket(String ip) {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(authLimit).refillIntervally(authLimit, Duration.ofMinutes(1)))
                .build();
    }

    private Bucket newGeneralBucket(String ip) {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(generalLimit).refillIntervally(generalLimit, Duration.ofMinutes(1)))
                .build();
    }
}
