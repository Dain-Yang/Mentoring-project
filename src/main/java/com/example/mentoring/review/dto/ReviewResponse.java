package com.example.mentoring.review.dto;

import com.example.mentoring.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
  // 리뷰 응답 DTO

  private Integer id;
  private Integer sessionId;
  private Integer reviewerId;
  private String reviewerNickname;
  private Integer targetId;
  private String targetNickname;
  private Integer rating;
  private String content;
  private LocalDateTime createdAt;

  public static ReviewResponse from(Review review) {
    return ReviewResponse.builder()
        .id(review.getId())
        .sessionId(review.getSession().getId())
        .reviewerId(review.getReviewer().getId())
        .reviewerNickname(review.getReviewer().getNickname())
        .targetId(review.getTarget().getId())
        .targetNickname(review.getTarget().getNickname())
        .rating(review.getRating())
        .content(review.getContent())
        .createdAt(review.getCreatedAt())
        .build();
  }
}