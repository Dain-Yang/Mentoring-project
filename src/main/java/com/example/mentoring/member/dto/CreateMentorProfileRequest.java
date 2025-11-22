package com.example.mentoring.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMentorProfileRequest {
  // 멘토 프로필 생성 요청 DTO

  @NotBlank(message = "멘토 소개는 필수입니다")
  private String mentorBio;

  private Integer careerYears;

  private String company;

  @NotNull(message = "직무를 선택해 주세요")
  private Integer fieldCodeId;

  @NotNull(message = "기술 수준을 선택해 주세요")
  private Integer levelCodeId;
}
