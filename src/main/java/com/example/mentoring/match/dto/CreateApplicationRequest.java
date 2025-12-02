package com.example.mentoring.match.dto;

import com.example.mentoring.match.entity.Application;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateApplicationRequest {
  // 신청서 제출 요청 DTO

  @NotBlank(message = "신청 내용은 필수입니다")
  private String content;

  public static ApplicationSummaryResponse from(Application application) {
    return ApplicationSummaryResponse.builder()
        .id(application.getId())
        .postId(application.getPost().getId())
        .postTitle(application.getPost().getTitle())
        .mentorNickname(application.getPost().getUser().getNickname())
        .menteeNickname(application.getUser().getNickname())
        .status(application.getStatus())
        .statusDescription(application.getStatus().getDescription())
        .appliedAt(application.getAppliedAt())
        .build();
  }
}