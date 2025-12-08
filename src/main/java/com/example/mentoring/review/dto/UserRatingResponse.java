package com.example.mentoring.review.dto;

import com.example.mentoring.member.entity.User;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingResponse {
  // 사용자 평점 통계 DTO

  private UUID userId;
  private String nickname;
  private Double averageRating;
  private Long reviewCount;

  public static UserRatingResponse of(User user, Double averageRating, Long reviewCount) {
    return UserRatingResponse.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .averageRating(averageRating)
        .reviewCount(reviewCount)
        .build();
  }
}