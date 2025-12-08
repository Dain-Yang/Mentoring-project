package com.example.mentoring.member.dto;

import com.example.mentoring.member.entity.MenteeProfile;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeProfileResponse {
  // 멘티 프로필 응답 DTO

  private UUID userId;
  private String menteeBio;
  // SystemCode의 display_name 반환
  private String fieldCode;
  private String levelCode;

  public static MenteeProfileResponse from(MenteeProfile profile) {
    return MenteeProfileResponse.builder()
        .userId(profile.getUserId())
        .menteeBio(profile.getMenteeBio())
        .fieldCode(profile.getFieldCode().getDisplayName())
        .levelCode(profile.getLevelCode().getDisplayName())
        .build();
  }
}
