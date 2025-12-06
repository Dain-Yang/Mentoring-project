package com.example.mentoring.match.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationRequest {
  // 신청서 수정 요청 DTO

  @NotBlank(message = "신청 내용은 필수입니다.")
  private String content;
}
