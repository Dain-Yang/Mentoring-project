package com.example.mentoring.match.entity;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.mentoring.member.entity.User;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "application", uniqueConstraints = {
    @UniqueConstraint(name = "uk_application_post_user", columnNames = {"post_id", "user_id"})
}) // 한 유저는 한 게시글에 한 번만 신청 가능
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Application {

  @Getter
  @RequiredArgsConstructor
  public enum ApplicationStatus {
    PENDING(0, "신청 접수"),
    APPROVED(1, "신청 승인"),
    REJECTED(2, "신청 거절");

    private final int code;
    private final String description;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // 신청자 (멘티)

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  // Enum 순서 변경 금지 (0:PENDING, 1:APPROVED, 2:REJECTED)
  @Enumerated(EnumType.ORDINAL)
  @Column(nullable = false)
  @Builder.Default
  private ApplicationStatus status = ApplicationStatus.PENDING;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime appliedAt;

  // 비즈니스 메서드
  public void approve() {
    validatePendingStatus();
    this.status = ApplicationStatus.APPROVED;
  }

  public void reject() {
    validatePendingStatus();
    this.status = ApplicationStatus.REJECTED;
  }

  public void updateContent(String content) {
    validatePendingStatus();
    this.content = content;
  }

  // 상태 검증 로직
  private void validatePendingStatus() {
    if (this.status != ApplicationStatus.PENDING) {
      throw new IllegalStateException("이미 처리된 신청서입니다.");
    }
  }
}