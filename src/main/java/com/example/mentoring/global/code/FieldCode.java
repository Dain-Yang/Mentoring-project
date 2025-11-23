package com.example.mentoring.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FieldCode {

  FRONT("프론트엔드"),
  BACK("백엔드"),
  MOBILE("모바일 개발"),
  DATA("데이터 분석"),
  DEVOPS("데브옵스/인프라");

  private final String displayName;


  // String 값으로 FieldCode를 찾는 헬퍼 메서드
  public static FieldCode fromValue(String value) {
    for (FieldCode code : FieldCode.values()) {
      if (code.name().equals(value)) {
        return code;
      }
    }
    throw new IllegalArgumentException("유효하지 않은 직무 코드입니다: " + value);
  }
}