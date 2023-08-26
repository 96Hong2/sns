package com.eunhong.sns.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

    // userName을 토큰에서 가져옴
    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName", String.class);
    }

    // 토큰이 만료되었는지 체크
    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date()); // 만료일자가 현재보다 이전인지(지났는지)
    }

    // 토큰에서 클레임즈를 파싱해서 가져오는 메소드
    private static Claims extractClaims(String token, String key){
        return Jwts.parserBuilder().setSigningKey(getKey(key))
                .build().parseClaimsJws(token).getBody();
    }

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
    public static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
