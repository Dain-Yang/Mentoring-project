package com.example.mentoring.match.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostSearchCondition {
  private String keyword;       // 제목 + 내용 검색어
  private List<Integer> tagIds; // 필터링할 태그 ID 목록
}