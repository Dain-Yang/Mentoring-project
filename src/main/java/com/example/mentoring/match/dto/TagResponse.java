package com.example.mentoring.match.dto;

import com.example.mentoring.match.entity.Tag;
import com.example.mentoring.match.entity.TagType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagResponse {
  // 태그 응답 DTO

  private Integer id;
  private String name; // Spring, 주 2회 등..
  private TagType type; // 기술 스택, 멘토링 방식 등..

  public static TagResponse from(Tag tag) {
    return TagResponse.builder()
        .id(tag.getId())
        .name(tag.getName())
        .type(tag.getType())
        .build();
  }
}