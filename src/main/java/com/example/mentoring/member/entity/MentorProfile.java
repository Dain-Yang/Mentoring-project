package com.example.mentoring.member.entity;

import com.example.mentoring.global.code.FieldCode;
import com.example.mentoring.global.code.LevelCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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

  @Enumerated(EnumType.STRING)
  @Column(name = "field_code", nullable = false)
  private FieldCode fieldCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "level_code", nullable = false)
  private LevelCode levelCode;

  // 비즈니스 메서드
  public void updateProfile(String mentorBio, Integer careerYears, String company,
      FieldCode fieldCode, LevelCode levelCode) {
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