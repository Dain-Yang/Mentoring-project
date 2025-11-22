package com.example.mentoring.member.entity;

import com.example.mentoring.systemcode.entity.SystemCode;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mentor_profile")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile {

  @Id
  private Integer userId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String mentorBio;

  @Column(nullable = false, precision = 2, scale = 1)
  @Builder.Default
  private BigDecimal avgRating = BigDecimal.ZERO; // BigDecimal for 데이터 무결성

  @Column(nullable = false)
  private Integer careerYears;

  @Column(length = 50)
  private String company;

  // 시스템 코드는 지연 로딩으로 처리
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "field_code_id", nullable = false)
  private SystemCode fieldCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "level_code_id", nullable = false)
  private SystemCode levelCode;

  // 비즈니스 메서드
  public void updateProfile(String mentorBio, Integer careerYears, String company,
      SystemCode fieldCode, SystemCode levelCode) {
    this.mentorBio = mentorBio;
    this.careerYears = careerYears;
    this.company = company;
    this.fieldCode = fieldCode;
    this.levelCode = levelCode;
  }

  public void updateAvgRating(BigDecimal newRating) {
    this.avgRating = newRating;
  }
}