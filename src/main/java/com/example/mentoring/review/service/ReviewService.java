package com.example.mentoring.review.service;

import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.repository.SessionRepository;
import com.example.mentoring.member.entity.MentorProfile;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.member.repository.UserRepository;
import com.example.mentoring.review.dto.CreateReviewRequest;
import com.example.mentoring.review.dto.ReviewResponse;
import com.example.mentoring.review.dto.ReviewSummaryResponse;
import com.example.mentoring.review.dto.UpdateReviewRequest;
import com.example.mentoring.review.dto.UserRatingResponse;
import com.example.mentoring.review.entity.Review;
import com.example.mentoring.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final SessionRepository sessionRepository;
  private final UserRepository userRepository;

  @Transactional
  public ReviewResponse createReview(Integer sessionId, Integer userId, CreateReviewRequest request) {
    User reviewer = getUser(userId);
    Session session = getSession(sessionId);

    if (reviewRepository.existsBySessionAndReviewer(session, reviewer)) {
      throw new IllegalStateException("이미 리뷰를 작성하였습니다.");
    }

    User target = getTargetUser(session, reviewer);

    Review review = Review.create(session, reviewer, target, request.getRating(), request.getContent());
    Review savedReview = reviewRepository.save(review);

    updateMentorRatingIfApplicable(target);

    return ReviewResponse.from(savedReview);
  }

  @Transactional
  public ReviewResponse updateReview(Integer reviewId, Integer userId, UpdateReviewRequest request) {
    Review review = getReviewEntity(reviewId);

    if (!review.getReviewer().getId().equals(userId)) {
      throw new IllegalStateException("본인이 작성한 리뷰만 수정할 수 있습니다.");
    }

    review.updateReview(request.getRating(), request.getContent());

    // 평점 재계산
    updateMentorRatingIfApplicable(review.getTarget());

    return ReviewResponse.from(review);
  }

  @Transactional
  public void deleteReview(Integer reviewId, Integer userId) {
    Review review = getReviewEntity(reviewId);

    if (!review.getReviewer().getId().equals(userId)) {
      throw new IllegalStateException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
    }

    User target = review.getTarget();
    reviewRepository.delete(review);

    updateMentorRatingIfApplicable(target);
  }

  // 조회 메서드

  public ReviewResponse getReview(Integer reviewId) {
    return ReviewResponse.from(getReviewEntity(reviewId));
  }

  public List<ReviewResponse> getReviewsBySession(Integer sessionId) {
    Session session = getSession(sessionId);
    return reviewRepository.findBySessionOrderByCreatedAtDesc(session).stream()
        .map(ReviewResponse::from)
        .collect(Collectors.toList());
  }

  public List<ReviewSummaryResponse> getReceivedReviews(Integer userId) {
    User user = getUser(userId);
    return reviewRepository.findByTargetOrderByCreatedAtDesc(user).stream()
        .map(ReviewSummaryResponse::from)
        .collect(Collectors.toList());
  }

  public List<ReviewSummaryResponse> getWrittenReviews(Integer userId) {
    User user = getUser(userId);
    return reviewRepository.findByReviewerOrderByCreatedAtDesc(user).stream()
        .map(ReviewSummaryResponse::from)
        .collect(Collectors.toList());
  }

  public UserRatingResponse getUserRating(Integer userId) {
    User user = getUser(userId);

    Double averageRating = reviewRepository.calculateAverageRatingByTarget(user);
    Long reviewCount = reviewRepository.countByTarget(user);

    return UserRatingResponse.of(user, roundRating(averageRating), reviewCount);
  }

  // 내부 헬퍼 메서드
  private User getUser(Integer userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
  }

  private Session getSession(Integer sessionId) {
    return sessionRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다."));
  }

  private Review getReviewEntity(Integer reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
  }

  private User getTargetUser(Session session, User reviewer) {
    if (session.getMentor().getId().equals(reviewer.getId())) {
      return session.getMentee();
    } else {
      return session.getMentor();
    }
  }

  // 멘토 프로필 평점 업데이트 로직
  private void updateMentorRatingIfApplicable(User target) {
    if (target.getRole() != User.Role.MENTOR) {
      return;
    }

    MentorProfile profile = target.getMentorProfile();
    if (profile == null) {
      return;
    }

    Double averageRating = reviewRepository.calculateAverageRatingByTarget(target);
    Long reviewCount = reviewRepository.countByTarget(target);

    // BigDecimal로 변환하여 소수점 1자리로 반올림
    BigDecimal roundedRating = BigDecimal.valueOf(averageRating)
        .setScale(1, RoundingMode.HALF_UP);

    profile.updateRating(roundedRating, reviewCount);
  }

  private Double roundRating(Double rating) {
    return Math.round(rating * 10.0) / 10.0;
  }
}