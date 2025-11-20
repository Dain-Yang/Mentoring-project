package com.example.mentoring.auth.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
  // 토큰 응답 DTO

  private String accessToken;

  @Builder.Default
  private String tokenType = "Bearer";
}