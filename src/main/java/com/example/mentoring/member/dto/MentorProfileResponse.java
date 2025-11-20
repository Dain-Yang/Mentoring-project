package com.example.mentoring.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfileResponse {
  // 멘토 프로필 응답 DTO

  private Integer userId;
  private String mentorBio;
  private Double avgRating;
  private Integer careerYears;
  private String company;
  // SystemCode의 display_name 반환
  private String fieldCode;
  private String levelCode;
}
