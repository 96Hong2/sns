package com.eunhong.sns.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

    public static String generateToken(String userName, String key, long expiredTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시각
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs)) // 토큰 만료 시각
                .signWith(getKey(key), SignatureAlgorithm.HS256) // 키는 getKey로 구해서 만들고 해시256 알고리즘 사용
                .compact(); // string token으로 리턴
    }

    // 토큰 키 값을 구하는 메서드
    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
