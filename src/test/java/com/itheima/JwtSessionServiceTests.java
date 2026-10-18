package com.itheima;

import com.itheima.config.JwtProperties;
import com.itheima.service.JwtSessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtSessionServiceTests {
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private JwtSessionService sessionService;
    private Claims claims;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        JwtProperties properties = new JwtProperties();
        properties.setSessionKeyPrefix("tlias:auth:session:");
        sessionService = new JwtSessionService(redisTemplate, properties);

        claims = new DefaultClaims();
        claims.setId("session-id");
        claims.setExpiration(new Date(System.currentTimeMillis() + 60_000));
    }

    @Test
    void createsSessionWithJwtRemainingLifetime() {
        sessionService.createSession("jwt-token", claims);

        ArgumentCaptor<Long> ttlCaptor = ArgumentCaptor.forClass(Long.class);
        verify(valueOperations).set(eq("tlias:auth:session:session-id"),
                eq(sha256("jwt-token")), ttlCaptor.capture(), eq(TimeUnit.MILLISECONDS));
        assertTrue(ttlCaptor.getValue() > 0 && ttlCaptor.getValue() <= 60_000);
    }

    @Test
    void acceptsOnlyMatchingPersistedToken() {
        when(valueOperations.get("tlias:auth:session:session-id"))
                .thenReturn(sha256("jwt-token"));

        assertTrue(sessionService.isActive("jwt-token", claims));
        assertFalse(sessionService.isActive("another-token", claims));
    }

    @Test
    void revokesSessionByJwtId() {
        sessionService.revoke(claims);

        verify(redisTemplate).delete("tlias:auth:session:session-id");
    }

    private String sha256(String value) {
        try {
            byte[] digest = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
