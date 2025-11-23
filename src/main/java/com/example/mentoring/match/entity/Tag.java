package com.example.mentoring.match.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tag", uniqueConstraints = {
    @UniqueConstraint(name = "uk_tag_name_type", columnNames = {"name", "type"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 생성 방지
@AllArgsConstructor
@Builder
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 50)
  private String name; //Spring, 주 2회 등..

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TagType type;

  // 생성 메서드 예시
  public static Tag create(String name, TagType type) {
    return Tag.builder()
        .name(name)
        .type(type)
        .build();
  }

}
