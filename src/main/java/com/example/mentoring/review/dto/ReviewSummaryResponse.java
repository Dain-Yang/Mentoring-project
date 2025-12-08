package com.example.mentoring.review.dto;

import com.example.mentoring.review.entity.Review;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryResponse {
  // 리뷰 목록 응답 (간략) DTO

  private Integer id;
  private UUID sessionId;
  private String reviewerNickname;
  private Integer rating;
  private String content;
  private LocalDateTime createdAt;

  public static ReviewSummaryResponse from(Review review) {
    return ReviewSummaryResponse.builder()
        .id(review.getId())
        .sessionId(review.getSession().getId())
        .reviewerNickname(review.getReviewer().getNickname())
        .rating(review.getRating())
        .content(review.getContent())
        .createdAt(review.getCreatedAt())
        .build();
  }
}