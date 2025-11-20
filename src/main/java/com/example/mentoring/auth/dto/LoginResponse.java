package com.example.mentoring.auth.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
  // 로그인 응답 DTO

  private String accessToken;

  // 최초 로그인 시 발급되며, accessToken 갱신 시에는 재발급되지 않음
  private String refreshToken;

  @Builder.Default
  private String tokenType = "Bearer";

  private Integer userId;
  private String email;
  private String nickname;
  private String role;
}