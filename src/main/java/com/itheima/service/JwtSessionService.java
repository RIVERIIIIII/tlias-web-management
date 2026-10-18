package com.itheima.service;

import com.itheima.config.JwtProperties;
import com.itheima.exception.JwtSessionUnavailableException;
import io.jsonwebtoken.Claims;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

@Service
public class JwtSessionService {
    private final StringRedisTemplate redisTemplate;
    private final JwtProperties properties;

    public JwtSessionService(StringRedisTemplate redisTemplate, JwtProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    public void createSession(String token, Claims claims) {
        long ttlMillis = remainingTtlMillis(claims);
        if (ttlMillis <= 0) {
            throw new IllegalArgumentException("不能持久化已过期的JWT");
        }
        try {
            redisTemplate.opsForValue().set(
                    sessionKey(claims), tokenHash(token), ttlMillis, TimeUnit.MILLISECONDS);
        } catch (DataAccessException e) {
            throw unavailable(e);
        }
    }

    public boolean isActive(String token, Claims claims) {
        if (remainingTtlMillis(claims) <= 0) {
            return false;
        }
        try {
            String persistedHash = redisTemplate.opsForValue().get(sessionKey(claims));
            return persistedHash != null && MessageDigest.isEqual(
                    persistedHash.getBytes(StandardCharsets.UTF_8),
                    tokenHash(token).getBytes(StandardCharsets.UTF_8));
        } catch (DataAccessException e) {
            throw unavailable(e);
        }
    }

    public void revoke(Claims claims) {
        try {
            redisTemplate.delete(sessionKey(claims));
        } catch (DataAccessException e) {
            throw unavailable(e);
        }
    }

    private String sessionKey(Claims claims) {
        String jwtId = claims.getId();
        if (jwtId == null || jwtId.isBlank()) {
            throw new IllegalArgumentException("JWT缺少jti");
        }
        return properties.getSessionKeyPrefix() + jwtId;
    }

    private long remainingTtlMillis(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration == null ? -1 : expiration.getTime() - System.currentTimeMillis();
    }

    private String tokenHash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前JVM不支持SHA-256", e);
        }
    }

    private JwtSessionUnavailableException unavailable(DataAccessException cause) {
        return new JwtSessionUnavailableException("JWT会话存储暂时不可用", cause);
    }
}
