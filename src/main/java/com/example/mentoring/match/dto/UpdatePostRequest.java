package com.example.mentoring.match.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {
  // 게시글 수정 요청 DTO

  @NotBlank(message = "제목은 필수입니다.")
  @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
  private String title;

  @NotBlank(message = "내용은 필수입니다.")
  private String content;

  @NotNull(message = "직무를 선택해 주세요")
  private String fieldCode;

  @NotNull(message = "기술 수준을 선택해 주세요")
  private String levelCode;

  private List<Integer> tagIds;
}