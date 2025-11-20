package com.example.mentoring.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
  // 사용자 응답 DTO

  private Integer id;
  private String email;
  private String nickname;
  private String role;
  private Boolean isActive;
  private String createdAt; // LocalDateTime을 String으로 포맷팅하여 반환
}
