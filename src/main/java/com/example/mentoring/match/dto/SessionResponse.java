package com.example.mentoring.match.dto;

import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.entity.Session.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
  // 세션 응답 DTO

  private Integer id;
  private SessionStatus status;
  private String statusDescription;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Boolean mentorConfirm;
  private Boolean menteeConfirm;
  private Integer mentorId;
  private String mentorNickname;
  private Integer menteeId;
  private String menteeNickname;
  private Integer applicationId;

  public static SessionResponse from(Session session) {
    return SessionResponse.builder()
        .id(session.getId())
        .status(session.getStatus())
        .statusDescription(session.getStatus().getDescription())
        .startDate(session.getStartDate())
        .endDate(session.getEndDate())
        .mentorConfirm(session.getMentorConfirm())
        .menteeConfirm(session.getMenteeConfirm())
        .mentorId(session.getMentor().getId())
        .mentorNickname(session.getMentor().getNickname())
        .menteeId(session.getMentee().getId())
        .menteeNickname(session.getMentee().getNickname())
        .applicationId(session.getApplication().getId())
        .build();
  }
}