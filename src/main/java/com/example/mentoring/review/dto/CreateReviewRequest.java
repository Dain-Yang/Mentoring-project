package com.example.mentoring.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
  // 리뷰 생성 요청 DTO

  @NotNull(message = "평점은 필수 입력 값입니다.")
  @Min(value = 1, message = "평점은 최소 1점이어야 합니다.")
  @Max(value = 5, message = "평점은 최대 5점이어야 합니다.")
  private Integer rating;

  @NotBlank(message = "리뷰 내용은 필수 입력 값입니다.")
  private String content;
}
