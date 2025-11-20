package com.example.mentoring.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMenteeProfileRequest {
  // 멘티 프로필 수정 요청 DTO

  private String menteeBio;
  private Integer fieldCodeId;
  private Integer levelCodeId;
}
