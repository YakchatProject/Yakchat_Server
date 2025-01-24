package com.kaidey.yakchatproject.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}") // application.yml에서 비밀 키 읽기
    private String SECRET_KEY;

    @Value("${jwt.expiration}") // application.yml에서 토큰 유효 시간 읽기
    private long EXPIRATION_TIME;

    @Value("${jwt.refreshExpiration}")
    private long REFRESH_EXPIRATION_TIME;


    // JWT 토큰 생성
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        return createToken(claims, username, EXPIRATION_TIME);
    }


    // JWT 리프레시 토큰 생성
    public String generateRefreshToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        return createToken(claims, username, REFRESH_EXPIRATION_TIME);
    }

    // JWT 토큰 생성 로직 분리
    private String createToken(Map<String, Object> claims, String subject, long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // JWT 토큰에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // JWT 토큰에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        Long userId = claims.get("userId", Long.class);
        if (userId == null) {
            throw new IllegalArgumentException("userId not found in token");
        }
        return userId;
    }


    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // 유효하지 않은 토큰
        }
    }

    // 리프레시 토큰을 갱신하고 새로운 액세스 토큰과 리프레시 토큰 반환
    public Map<String, String> refreshTokens(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = getUsernameFromToken(refreshToken);
        Long userId = getUserIdFromToken(refreshToken);

        // 새 액세스 토큰과 리프레시 토큰 생성
        String newAccessToken = generateToken(username, userId);
        String newRefreshToken = generateRefreshToken(username, userId);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }
}
