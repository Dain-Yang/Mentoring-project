package com.example.mentoring.match.dto;

import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.entity.Session.SessionStatus;
import com.example.mentoring.member.entity.User;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionSummaryResponse {
  // 세션 목록 응답 (간략) DTO
  private UUID sessionId;
  private String statusDescription;
  private String partnerNickname; // 상대방 닉네임
  private LocalDateTime startDate;

  // 상대방(Partner)을 계산해야 하므로 currentUser를 인자로 받음
  public static SessionSummaryResponse of(Session session, User currentUser) {
    User partner = session.getMentor().getId().equals(currentUser.getId())
        ? session.getMentee()
        : session.getMentor();

    return SessionSummaryResponse.builder()
        .sessionId(session.getId())
        .statusDescription(session.getStatus().getDescription())
        .partnerNickname(partner.getNickname())
        .startDate(session.getStartDate())
        .build();
  }
}