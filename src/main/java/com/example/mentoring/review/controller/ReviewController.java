package com.example.mentoring.review.controller;

import com.example.mentoring.review.dto.CreateReviewRequest;
import com.example.mentoring.review.dto.ReviewResponse;
import com.example.mentoring.review.dto.ReviewSummaryResponse;
import com.example.mentoring.review.dto.UpdateReviewRequest;
import com.example.mentoring.review.dto.UserRatingResponse;
import com.example.mentoring.review.service.ReviewService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  // 리뷰 작성
  @PostMapping("/session/{sessionId}")
  public ResponseEntity<ReviewResponse> createReview(
      @PathVariable UUID sessionId,
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody CreateReviewRequest request) {
    ReviewResponse response = reviewService.createReview(sessionId, userId, request);
    return ResponseEntity.ok(response);
  }

  // 리뷰 수정
  @PutMapping("/{reviewId}")
  public ResponseEntity<ReviewResponse> updateReview(
      @PathVariable Integer reviewId,
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody UpdateReviewRequest request) {
    ReviewResponse response = reviewService.updateReview(reviewId, userId, request);
    return ResponseEntity.ok(response);
  }

  // 리뷰 삭제
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<Void> deleteReview(
      @PathVariable Integer reviewId,
      @AuthenticationPrincipal UUID userId) {
    reviewService.deleteReview(reviewId, userId);
    return ResponseEntity.noContent().build();
  }

  // 리뷰 상세 조회
  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewResponse> getReview(@PathVariable Integer reviewId) {
    ReviewResponse response = reviewService.getReview(reviewId);
    return ResponseEntity.ok(response);
  }

  // 세션의 모든 리뷰 조회
  @GetMapping("/session/{sessionId}")
  public ResponseEntity<List<ReviewResponse>> getReviewsBySession(@PathVariable UUID sessionId) {
    List<ReviewResponse> responses = reviewService.getReviewsBySession(sessionId);
    return ResponseEntity.ok(responses);
  }

  // 받은 리뷰 목록 조회
  @GetMapping("/user/{userId}/received")
  public ResponseEntity<List<ReviewSummaryResponse>> getReceivedReviews(
      @PathVariable UUID userId) {
    List<ReviewSummaryResponse> responses = reviewService.getReceivedReviews(userId);
    return ResponseEntity.ok(responses);
  }

  // 작성한 리뷰 목록 조회
  @GetMapping("/user/{userId}/written")
  public ResponseEntity<List<ReviewSummaryResponse>> getWrittenReviews(
      @PathVariable UUID userId) {
    List<ReviewSummaryResponse> responses = reviewService.getWrittenReviews(userId);
    return ResponseEntity.ok(responses);
  }

  // 내가 받은 리뷰 목록 조회 (편의 기능)
  @GetMapping("/my/received")
  public ResponseEntity<List<ReviewSummaryResponse>> getMyReceivedReviews(
      @AuthenticationPrincipal UUID userId) {
    List<ReviewSummaryResponse> responses = reviewService.getReceivedReviews(userId);
    return ResponseEntity.ok(responses);
  }

  // 내가 작성한 리뷰 목록 조회 (편의 기능)
  @GetMapping("/my/written")
  public ResponseEntity<List<ReviewSummaryResponse>> getMyWrittenReviews(
      @AuthenticationPrincipal UUID userId) {
    List<ReviewSummaryResponse> responses = reviewService.getWrittenReviews(userId);
    return ResponseEntity.ok(responses);
  }

  // 사용자 평점 통계 조회
  @GetMapping("/user/{userId}/rating")
  public ResponseEntity<UserRatingResponse> getUserRating(@PathVariable UUID userId) {
    UserRatingResponse response = reviewService.getUserRating(userId);
    return ResponseEntity.ok(response);
  }
}