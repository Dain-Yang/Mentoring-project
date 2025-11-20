package com.example.mentoring.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRefreshRequest {
  // 토큰 갱신 요청 DTO

  @NotBlank(message = "리프레시 토큰은 필수입니다.")
  private String refreshToken;
}