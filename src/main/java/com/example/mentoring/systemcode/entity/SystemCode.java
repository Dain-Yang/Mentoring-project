package com.example.mentoring.systemcode.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
// type과 value의 조합이 유일해야 함
@Table(name = "system_code", uniqueConstraints = {
    @UniqueConstraint(name = "uk_system_code_type_value", columnNames = {"type", "value"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false, length = 20)
  private String type; // FIELD(직무), LEVEL(실력수준)

  @Column(nullable = false, length = 20)
  private String value; // BACK, FRONT / LV0, LV1

  @Column(nullable = false, length = 50)
  private String displayName; // 백엔드, 프론트엔드 / 초급, 중급
}