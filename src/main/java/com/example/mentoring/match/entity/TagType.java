package com.example.mentoring.match.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TagType {

  STACK("기술 스택"), // 예: Spring, React, Java
  METHOD("멘토링 방식"); // 예: 코드리뷰, 이력서 첨삭, 커피챗

  private final String description;

  // String -> Enum 변환 메서드
  public static TagType from(String value) {
    for (TagType type : TagType.values()) {
      if (type.name().equals(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("유효하지 않은 태그 타입입니다: " + value);
  }
}