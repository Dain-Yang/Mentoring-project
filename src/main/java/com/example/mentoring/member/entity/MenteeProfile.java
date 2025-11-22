package com.example.mentoring.member.entity;

import com.example.mentoring.systemcode.entity.SystemCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "mentee_profile")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeProfile {

  @Id
  private Integer userId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String menteeBio;

  // 시스템 코드는 지연 로딩으로 처리
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "field_code_id", nullable = false)
  private SystemCode fieldCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "level_code_id", nullable = false)
  private SystemCode levelCode;

  // 비즈니스 메서드
  public void updateProfile(String menteeBio, SystemCode fieldCode, SystemCode levelCode) {
    this.menteeBio = menteeBio;
    this.fieldCode = fieldCode;
    this.levelCode = levelCode;
  }
}