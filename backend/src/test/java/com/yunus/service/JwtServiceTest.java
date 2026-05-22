package com.yunus.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Unit Testleri")
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    // 32 byte (256-bit) rastgele bir Base64 secret key
    private static final String TEST_SECRET =
            Encoders.BASE64.encode(Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded());

    private static final long JWT_EXPIRATION_MS = 3_600_000L; // 1 saat

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // @Value ile inject edilen alanları ReflectionTestUtils ile set ediyoruz
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", JWT_EXPIRATION_MS);

        userDetails = User.builder()
                .username("testuser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();
    }

    // ─────────────────────────────────────────────
    // generateToken
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("generateToken: Token null olmamalı ve boş olmamalı")
    void generateToken_shouldReturnNonNullToken() {
        // When
        String token = jwtService.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("generateToken: Token üç parçadan oluşan JWT formatında olmalı (header.payload.signature)")
    void generateToken_shouldReturnJwtFormatToken() {
        // When
        String token = jwtService.generateToken(userDetails);

        // Then – JWT formatı: header.payload.signature
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token 3 parçadan oluşmalı");
    }

    // ─────────────────────────────────────────────
    // extractUsername
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("extractUsername: Token içinden kullanıcı adı doğru çıkarılmalı")
    void extractUsername_shouldReturnCorrectUsername() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals("testuser", extractedUsername);
    }

    // ─────────────────────────────────────────────
    // isTokenValid
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("isTokenValid: Geçerli token ve eşleşen kullanıcı için true dönmeli")
    void isTokenValid_shouldReturnTrue_whenTokenIsValidAndNotExpired() {
        // Given
        String token = jwtService.generateToken(userDetails);

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("isTokenValid: Kullanıcı adı eşleşmiyorsa false dönmeli")
    void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch() {
        // Given – token "testuser" için üretildi
        String token = jwtService.generateToken(userDetails);

        // Farklı bir kullanıcı
        UserDetails anotherUser = User.builder()
                .username("anotheruser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // When
        boolean isValid = jwtService.isTokenValid(token, anotherUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("isTokenValid: Süresi dolmuş token için false dönmeli")
    void isTokenValid_shouldReturnFalse_whenTokenIsExpired() {
        // Given – jwtExpiration'ı -1 yaparak anında süresi dolmuş token oluştur
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1L);
        String expiredToken = jwtService.generateToken(userDetails);

        // Expiration'ı normal değere geri al (kullanıcı adı extraction için)
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", JWT_EXPIRATION_MS);

        // When & Then
        // Süresi dolmuş token parse edilirken exception fırlatılır
        assertThrows(Exception.class, () -> jwtService.isTokenValid(expiredToken, userDetails));
    }

    @Test
    @DisplayName("generateToken: Farklı kullanıcılar için farklı tokenlar üretilmeli")
    void generateToken_shouldReturnDifferentTokensForDifferentUsers() {
        // Given
        UserDetails anotherUser = User.builder()
                .username("anotheruser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // When
        String token1 = jwtService.generateToken(userDetails);
        String token2 = jwtService.generateToken(anotherUser);

        // Then
        assertNotEquals(token1, token2);
    }
}
