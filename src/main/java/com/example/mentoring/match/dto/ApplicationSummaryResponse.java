package com.example.mentoring.match.dto;

import com.example.mentoring.match.entity.Application;
import com.example.mentoring.match.entity.Application.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSummaryResponse {
  // 신청서 목록 응답 DTO (마이페이지용)

  private UUID id;
  private Integer postId;
  private String postTitle; // 클릭하면 해당 모집글로 이동
  private String mentorNickname;
  private String menteeNickname;

  private String statusDescription;
  private LocalDateTime appliedAt;

  public static ApplicationSummaryResponse from(Application application) {
    return ApplicationSummaryResponse.builder()
        .id(application.getId())
        .postId(application.getPost().getId())
        .postTitle(application.getPost().getTitle())
        .mentorNickname(application.getPost().getUser().getNickname())
        .menteeNickname(application.getUser().getNickname())
        .statusDescription(application.getStatus().getDescription())
        .appliedAt(application.getAppliedAt())
        .build();
  }
}
