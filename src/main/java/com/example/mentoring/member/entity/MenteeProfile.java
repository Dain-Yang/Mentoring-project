package com.example.mentoring.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


  @Enumerated(EnumType.STRING)
  @Column(name = "field_code", nullable = false)
  private FieldCode fieldCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "level_code", nullable = false)
  private LevelCode levelCode;

  // 비즈니스 메서드
  public void updateProfile(String menteeBio, FieldCode fieldCode, LevelCode levelCode) {
    this.menteeBio = menteeBio;
    this.fieldCode = fieldCode;
    this.levelCode = levelCode;
  }
}