package com.example.mentoring.match.entity;

import com.example.mentoring.global.code.FieldCode;
import com.example.mentoring.global.code.LevelCode;
import com.example.mentoring.member.entity.User;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user; // 작성자 (멘토)

  @Enumerated(EnumType.STRING)
  @Column(name = "field_code", nullable = false)
  private FieldCode fieldCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "level_code", nullable = false)
  private LevelCode levelCode;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "is_recruiting", nullable = false)
  @Builder.Default
  private Boolean isRecruiting = true; // TRUE: 모집 중, FALSE: 모집 완료

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column
  private LocalDateTime updatedAt;

  // CascadeType.ALL + orphanRemoval = true : 게시글 삭제 시 태그 연결 및 신청서도 함께 삭제됨
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PostTag> postTags = new ArrayList<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Application> applications = new ArrayList<>();

  // 비즈니스 메서드

  public void updatePost(String title, String content, FieldCode fieldCode, LevelCode levelCode) {
    this.title = title;
    this.content = content;
    this.fieldCode = fieldCode;
    this.levelCode = levelCode;
  }

  public boolean isRecruiting() {
    return Boolean.TRUE.equals(this.isRecruiting);
  }

  public void closeRecruitment() {
    this.isRecruiting = false;
  }

  public void openRecruitment() {
    this.isRecruiting = true;
  }

  // 연관관계 편의 메서드 (태그 추가)
  public void addPostTag(PostTag postTag) {
    this.postTags.add(postTag);
    postTag.setPost(this); // 양방향 연관관계 설정
  }

  // 태그 목록 초기화 (수정 시 기존 태그 삭제 용도)
  public void clearPostTags() {
    this.postTags.clear();
  }
}