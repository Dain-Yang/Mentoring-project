package com.example.mentoring.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
  // 게시글 생성 요청 DTO

  @NotBlank(message = "제목은 필수입니다")
  @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
  private String title;

  @NotBlank(message = "내용은 필수입니다")
  private String content;

  @NotNull(message = "직무를 선택해 주세요")
  private String fieldCode;

  @NotNull(message = "기술 수준을 선택해 주세요")
  private String levelCode;

  // 태그는 선택사항일 수 있으므로 @NotNull 제외, 하지만 리스트 자체는 null이 아님
  private List<Integer> tagIds;
}