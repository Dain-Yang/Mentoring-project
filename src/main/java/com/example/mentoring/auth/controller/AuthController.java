package com.example.mentoring.auth.controller;

import com.example.mentoring.auth.dto.LoginRequest;
import com.example.mentoring.auth.dto.LoginResponse;
import com.example.mentoring.auth.dto.TokenRefreshRequest;
import com.example.mentoring.auth.dto.TokenResponse;
import com.example.mentoring.auth.service.AuthService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  // 토큰 재발급 (Refresh)
  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
    TokenResponse response = authService.refresh(request);
    return ResponseEntity.ok(response);
  }


  // 로그아웃
  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    authService.logout();
    return ResponseEntity.ok().build();
  }
}