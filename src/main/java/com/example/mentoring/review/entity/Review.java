package com.example.mentoring.review.entity;

import com.example.mentoring.match.entity.Session;
import com.example.mentoring.member.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "review",
    // 한 세션에 대해 작성자는 하나의 리뷰만 작성 가능
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_review_session_reviewer",
            columnNames = {"session_id", "reviewer_user_id"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false)
  private Session session;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reviewer_user_id", nullable = false)
  private User reviewer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_user_id", nullable = false)
  private User target;

  @Column(nullable = false)
  private Integer rating; // 1 ~ 5

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // 비즈니스 로직
  public void updateReview(Integer rating, String content) {
    validateRating(rating);
    this.rating = rating;
    this.content = content;
  }

  private void validateRating(Integer rating) {
    if (rating == null || rating < 1 || rating > 5) {
      throw new IllegalArgumentException("평점은 1에서 5 사이여야 합니다.");
    }
  }

  // 팩토리 메서드
  public static Review create(Session session, User reviewer, User target, Integer rating, String content) {
    if (!session.isCompleted()) {
      throw new IllegalStateException("완료된 멘토링 세션에 대해서만 리뷰를 작성할 수 있습니다.");
    }

    if (!session.isParticipant(reviewer)) {
      throw new IllegalArgumentException("해당 멘토링 세션의 참여자만 리뷰를 작성할 수 있습니다.");
    }

    if (reviewer.getId().equals(target.getId())) {
      throw new IllegalArgumentException("자신에게 리뷰를 작성할 수 없습니다.");
    }

    Review review = Review.builder()
        .session(session)
        .reviewer(reviewer)
        .target(target)
        .rating(rating)
        .content(content)
        .build();

    review.validateRating(rating);

    return review;
  }
}