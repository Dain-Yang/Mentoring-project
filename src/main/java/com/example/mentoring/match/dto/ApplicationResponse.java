package com.example.mentoring.match.dto;

import com.example.mentoring.match.entity.Application;
import com.example.mentoring.match.entity.Application.ApplicationStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
  // 신청서 목록 (상세) 응답 DTO

  private Integer id;

  // 어떤 글에 대한 신청인지
  private Integer postId;
  private String postTitle;

  // 누가 신청했는지 (멘토가 확인)
  private Integer menteeId;
  private String menteeNickname;

  private String content;

  private ApplicationStatus status;
  private String statusDescription;

  private LocalDateTime appliedAt;

  public static ApplicationResponse from(Application application) {
    return ApplicationResponse.builder()
        .id(application.getId())
        .postId(application.getPost().getId())
        .postTitle(application.getPost().getTitle())
        .menteeId(application.getUser().getId())
        .menteeNickname(application.getUser().getNickname())
        .content(application.getContent())
        .status(application.getStatus())
        .statusDescription(application.getStatus().getDescription())
        .appliedAt(application.getAppliedAt())
        .build();
  }
}