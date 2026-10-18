package com.itheima.utils;

import com.itheima.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtils {

    private final JwtProperties properties;

    public JwtUtils(JwtProperties properties) {
        this.properties = properties;
    }

    /**
     * 生成JWT令牌
     * @return
     */
    public String generateJwt(Map<String,Object> claims){
        Date issuedAt = new Date();
        String jwt = Jwts.builder()
                .addClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(issuedAt)
                .signWith(SignatureAlgorithm.HS256, properties.getSignKey())
                .setExpiration(new Date(issuedAt.getTime() + properties.getExpireMs()))
                .compact();
        return jwt;
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public Claims parseJWT(String jwt){
        Claims claims = Jwts.parser()
                .setSigningKey(properties.getSignKey())
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
}
