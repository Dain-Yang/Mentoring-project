package com.example.mentoring.member.dto;

import com.example.mentoring.member.entity.MentorProfile;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfileResponse {
  // 멘토 프로필 응답 DTO

  private UUID userId;
  private String mentorBio;
  private Double avgRating;
  private Long reviewCount;
  private Integer careerYears;
  private String company;

  // SystemCode의 display_name 반환
  private String fieldCode;
  private String levelCode;

  public static MentorProfileResponse from(MentorProfile profile) {
    return MentorProfileResponse.builder()
        .userId(profile.getUserId())
        .mentorBio(profile.getMentorBio())
        // BigDecimal -> Double 변환 및 null 처리 로직
        .avgRating(profile.getAvgRating() != null ? profile.getAvgRating().doubleValue() : 0.0)
        .reviewCount(profile.getReviewCount())
        .careerYears(profile.getCareerYears())
        .company(profile.getCompany())
        .fieldCode(profile.getFieldCode().getDisplayName())
        .levelCode(profile.getLevelCode().getDisplayName())
        .build();
  }
}
