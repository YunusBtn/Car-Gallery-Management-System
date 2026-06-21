package com.yunus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimiterService Unit Testleri")
class RateLimiterServiceTest {

    @InjectMocks
    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rateLimiterService, "authLimit", 2);
        ReflectionTestUtils.setField(rateLimiterService, "generalLimit", 5);
    }

    @Test
    @DisplayName("tryConsume: Auth endpointleri için belirtilen limit kadar istek kabul edilmeli, aşınca reddedilmeli")
    void tryConsume_shouldEnforceAuthLimit() {
        String ip = "192.168.1.1";
        String path = "/api/auth/login";

        // İlk iki istek başarılı olmalı (limit 2)
        assertTrue(rateLimiterService.tryConsume(ip, path));
        assertTrue(rateLimiterService.tryConsume(ip, path));

        // Üçüncü istek limit aşıldığı için reddedilmeli
        assertFalse(rateLimiterService.tryConsume(ip, path));
    }

    @Test
    @DisplayName("tryConsume: Genel endpointler için belirtilen limit kadar istek kabul edilmeli, aşınca reddedilmeli")
    void tryConsume_shouldEnforceGeneralLimit() {
        String ip = "192.168.1.2";
        String path = "/api/car/list";

        // İlk beş istek başarılı olmalı (limit 5)
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiterService.tryConsume(ip, path));
        }

        // Altıncı istek limit aşıldığı için reddedilmeli
        assertFalse(rateLimiterService.tryConsume(ip, path));
    }

    @Test
    @DisplayName("tryConsume: Farklı IP adresleri için limitler bağımsız çalışmalı")
    void tryConsume_shouldLimitIndependentlyForDifferentIPs() {
        String ip1 = "192.168.1.10";
        String ip2 = "192.168.1.20";
        String path = "/api/auth/login";

        // IP1 limitini tüketsin
        assertTrue(rateLimiterService.tryConsume(ip1, path));
        assertTrue(rateLimiterService.tryConsume(ip1, path));
        assertFalse(rateLimiterService.tryConsume(ip1, path));

        // IP2 limitleri etkilenmemiş olmalı
        assertTrue(rateLimiterService.tryConsume(ip2, path));
        assertTrue(rateLimiterService.tryConsume(ip2, path));
        assertFalse(rateLimiterService.tryConsume(ip2, path));
    }
}
