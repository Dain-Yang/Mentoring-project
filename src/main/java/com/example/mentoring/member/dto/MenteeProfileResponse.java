package com.example.mentoring.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeProfileResponse {
  // 멘티 프로필 응답 DTO

  private Integer userId;
  private String menteeBio;
  // SystemCode의 display_name 반환
  private String fieldCode;
  private String levelCode;
}
