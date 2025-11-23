package com.example.mentoring.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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