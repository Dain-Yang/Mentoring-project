package com.example.mentoring.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMentorProfileRequest {
  // 멘토 프로필 수정 요청 DTO

  private String mentorBio;
  private Integer careerYears;
  private String company;
  private Integer fieldCodeId;
  private Integer levelCodeId;
}
