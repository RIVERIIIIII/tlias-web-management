package com.itheima;

import com.itheima.config.JwtProperties;
import com.itheima.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class jwtTest {
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSignKey("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        properties.setExpireMs(3600_000L);
        jwtUtils = new JwtUtils(properties);
    }

    @Test
    public void testGenerateJwt() {
        Map<String, Object> claims = Map.of("username", "123456");
        String jwt = jwtUtils.generateJwt(claims);
        assertNotNull(jwt);
    }

    @Test
    public void testParseJwt() {
        String token = jwtUtils.generateJwt(Map.of("username", "123456"));
        Claims body = jwtUtils.parseJWT(token);
        assertEquals("123456", body.get("username"));
    }
}
