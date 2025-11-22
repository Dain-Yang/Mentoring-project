package com.example.mentoring.member.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  public enum Role {
    MENTOR,
    MENTEE;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, unique = true, length = 225)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true, length = 10)
  private String nickname;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private Role role;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isActive = true;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private MentorProfile mentorProfile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private MenteeProfile menteeProfile;

  // 비즈니스 메서드
  public void updatePassword(String newPassword) {
    this.password = newPassword;
  }

  public void updateNickname(String newNickname) {
    this.nickname = newNickname;
  }

  public void updateRole(Role newRole) {
    this.role = newRole;
  }

  public void deactivate() {
    this.isActive = false;
  }

  public void activate() {
    this.isActive = true;
  }
}