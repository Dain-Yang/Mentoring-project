package com.example.mentoring.member.dto;

import com.example.mentoring.member.entity.User;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
  // 사용자 응답 DTO

  private UUID id;
  private String email;
  private String nickname;
  private String role;
  private Boolean isActive;
  private String createdAt;

  // Entity -> DTO 변환 로직 정적 팩토리 메서드로 이동
  public static UserResponse from(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .role(user.getRole().name())
        .isActive(user.getIsActive())
        // 날짜 포맷팅 로직 처리
        .createdAt(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        .build();
  }
}
