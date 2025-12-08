package com.example.mentoring.match.entity;

import com.example.mentoring.member.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "session")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

  @Getter
  public enum SessionStatus {
    PROGRESS("멘토링 진행 중"),
    END_REQUESTED("멘토링 종료 요청"),
    COMPLETED("멘토링 종료");

    private final String description;

    SessionStatus(String description) {
      this.description = description;
    }
  }

  // Integer -> UUID 변경
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  // 순서 대신 STRING으로 관리
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private SessionStatus status = SessionStatus.PROGRESS;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime startDate;

  @Column
  private LocalDateTime endDate;

  @Column(nullable = false)
  @Builder.Default
  private Boolean mentorConfirm = false;

  @Column(nullable = false)
  @Builder.Default
  private Boolean menteeConfirm = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_user_id", nullable = false)
  private User mentor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentee_user_id", nullable = false)
  private User mentee;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_id", nullable = false, unique = true)
  private Application application;

  // 비즈니스 메서드
  public void requestEnd(User requester) {
    if (this.status != SessionStatus.PROGRESS) {
      throw new IllegalStateException("진행 중인 멘토링만 종료 요청할 수 있습니다.");
    }

    this.status = SessionStatus.END_REQUESTED;

    // 종료 요청자 확인 처리
    if (requester.getId().equals(this.mentor.getId())) {
      this.mentorConfirm = true;
    } else if (requester.getId().equals(this.mentee.getId())) {
      this.menteeConfirm = true;
    } else {
      throw new IllegalStateException("멘토링 참여자만 종료 요청할 수 있습니다.");
    }
  }

  public void confirmEnd(User confirmer) {
    if (this.status != SessionStatus.END_REQUESTED) {
      throw new IllegalStateException("종료 요청 상태에서만 확인할 수 있습니다.");
    }

    // 상대방 확인 처리
    if (confirmer.getId().equals(this.mentor.getId())) {
      this.mentorConfirm = true;
    } else if (confirmer.getId().equals(this.mentee.getId())) {
      this.menteeConfirm = true;
    } else {
      throw new IllegalStateException("멘토링 참여자만 확인할 수 있습니다.");
    }

    // 양쪽 모두 확인하면 완료 처리
    if (this.mentorConfirm && this.menteeConfirm) {
      this.status = SessionStatus.COMPLETED;
      this.endDate = LocalDateTime.now();
    }
  }

  public boolean isParticipant(User user) {
    return this.mentor.getId().equals(user.getId()) ||
        this.mentee.getId().equals(user.getId());
  }

  public boolean isCompleted() {
    return this.status == SessionStatus.COMPLETED;
  }
}