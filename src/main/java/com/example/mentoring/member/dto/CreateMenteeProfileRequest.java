package com.example.mentoring.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMenteeProfileRequest {
  // 멘티 프로필 생성 요청 DTO

  // 멘티는 희망 직무와 기술 수준만 기입해도 최소 소개는 된다고 판단해서 제약조건 없앰
  private String menteeBio;

  @NotNull(message = "직무를 선택해 주세요")
  private Integer fieldCodeId;

  @NotNull(message = "기술 수준을 선택해 주세요")
  private Integer levelCodeId;
}
