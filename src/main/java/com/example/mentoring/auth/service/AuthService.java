package com.example.mentoring.auth.service;

import com.example.mentoring.auth.dto.LoginRequest;
import com.example.mentoring.auth.dto.LoginResponse;
import com.example.mentoring.auth.dto.TokenRefreshRequest;
import com.example.mentoring.auth.dto.TokenResponse;
import com.example.mentoring.global.util.JwtUtil;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import lombok.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository; // for 토큰 재발급

  // 로그인
  public LoginResponse login(LoginRequest request) {
    // 인증 수행
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 인증 객체에서 User 정보 추출
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    User user = userDetails.getUser();

    // 토큰 발급
    String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
    String refreshToken = jwtUtil.createRefreshToken(user.getId());

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole().name())
        .build();
  }

  // 토큰 재발급
  public TokenResponse refresh(TokenRefreshRequest request) {
    String refreshToken = request.getRefreshToken();

    // Refresh Token 유효성 검증
    if (!jwtUtil.validateToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다");
    }

    // 사용자 ID 추출 및 DB 조회
    Integer userId = jwtUtil.getUserIdFromToken(refreshToken);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

    // 새 Access Token 생성
    String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());

    // 응답 (Access Token만 반환)
    return TokenResponse.builder()
        .accessToken(newAccessToken)
        .tokenType("Bearer")
        .build();
  }

  // 로그아웃
  public void logout() {
    SecurityContextHolder.clearContext();
  }

  // 현재 사용자 정보 조회
  public CustomUserDetails getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()
        || authentication.getPrincipal() instanceof String) { // anonymousUser 체크
      throw new IllegalStateException("인증되지 않은 사용자입니다");
    }

    return (CustomUserDetails) authentication.getPrincipal();
  }
}