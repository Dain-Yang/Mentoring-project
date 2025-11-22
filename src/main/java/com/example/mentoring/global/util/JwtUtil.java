package com.example.mentoring.global.util;

import com.example.mentoring.member.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

  private final SecretKey key;
  private final long accessTokenValidityInMilliseconds;
  private final long refreshTokenValidityInMilliseconds;

  public JwtUtil(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.access-token-validity}") long accessTokenValidityInMilliseconds,
      @Value("${jwt.refresh-token-validity}") long refreshTokenValidityInMilliseconds) {

    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
    this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
  }

  // Access Token 생성
  public String createAccessToken(Integer userId, String email, User.Role role) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

    return Jwts.builder()
        .subject(userId.toString())
        .claim("email", email)
        .claim("role", role.name())
        .issuedAt(now)
        .expiration(validity)
        .signWith(key, Jwts.SIG.HS256)
        .compact();
  }

  // Refresh Token 생성
  public String createRefreshToken(Integer userId) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(now)
        .expiration(validity)
        .signWith(key, Jwts.SIG.HS256)
        .compact();
  }

  // 토큰 유효성 검증
  public boolean validateToken(String token) {
    try {
      // verifyWith 적용
      Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (SecurityException | MalformedJwtException | SignatureException e) {
      log.error("유효하지 않거나 손상된 JWT 서명입니다: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
    }
    return false;
  }

  // Claims 파싱
  private Claims parseClaims(String token) {
    try {
      // verifyWith 적용
      return Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

  // 토큰 정보 추출
  public Integer getUserIdFromToken(String token) {
    Claims claims = parseClaims(token);
    return Integer.parseInt(claims.getSubject());
  }

  public String getEmailFromToken(String token) {
    Claims claims = parseClaims(token);
    return claims.get("email", String.class);
  }

  public String getRoleFromToken(String token) {
    Claims claims = parseClaims(token);
    return claims.get("role", String.class);
  }
}