package com.itheima;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class jwtTest {
    @Test
    public void testGenerateJwt() {
        Map<String, Object> claims = Map.of("username", "123456");
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, "aXRoZWltYQ==")
                .addClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000))
                .compact();
        System.out.println(jwt);
    }

    @Test
    public void testParseJwt() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IjEyMzQ1NiIsImV4cCI6MTc2MTE2MjE3Nn0.EyzliIlyM37PtxkvVbvpc4LdX4aEaV5Efx-wFniHQGs";
        Claims body = Jwts.parser()
                .setSigningKey("aXRoZWltYQ==")
                .parseClaimsJws(token)
                .getBody();
        System.out.println(body);
    }
}
