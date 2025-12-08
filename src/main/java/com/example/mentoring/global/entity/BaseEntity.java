package com.example.mentoring.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
  // 공통 시간 관리 엔티티

  @CreatedDate
  @Column(updatable = false) // 생성 시간은 수정되지 않도록 설정
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt; // 수정될 때마다 자동 업데이트
}