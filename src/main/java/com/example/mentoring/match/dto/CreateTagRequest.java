package com.example.mentoring.match.dto;

import com.example.mentoring.match.entity.TagType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTagRequest {
  // 태그 생성 DTO

  @NotBlank(message = "태그 이름은 필수입니다. (ex: Spring, 주 2회 등)")
  private String name;
  @NotNull(message = "태그 타입은 필수입니다. (ex: 기술 스택, 멘토링 방식 등)")
  private TagType type;
}
