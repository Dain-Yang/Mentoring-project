package com.example.mentoring.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

//기술 수준 코드 관리 Enum
@Getter
@RequiredArgsConstructor
public enum LevelCode {

  LV0("입문 (Beginner)", 1),
  LV1("초급 (Junior)", 2),
  LV2("중급 (Intermediate)", 3),
  LV3("고급 (Senior)", 4),
  LV4("전문가 (Expert)", 5);

  private final String displayName;
  private final int order; // 정렬 순서

  //String 값으로 LevelCode를 찾는 헬퍼 메서드
  public static LevelCode fromValue(String value) {
    for (LevelCode code : LevelCode.values()) {
      if (code.name().equals(value)) {
        return code;
      }
    }
    throw new IllegalArgumentException("유효하지 않은 레벨 코드입니다: " + value);
  }
}