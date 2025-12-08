package com.example.mentoring.review.repository;

import com.example.mentoring.match.entity.Session;
import com.example.mentoring.member.entity.User;
import com.example.mentoring.review.entity.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

  // 중복 조회 방지
  Optional<Review> findBySessionAndReviewer(Session session, User reviewer);

  boolean existsBySessionAndReviewer(Session session, User reviewer);

  // 목록 조회 시 작성자와 세션 정보를 함께 로딩
  @EntityGraph(attributePaths = {"reviewer", "session"})
  List<Review> findByTargetOrderByCreatedAtDesc(User target);

  @EntityGraph(attributePaths = {"target", "session"})
  List<Review> findByReviewerOrderByCreatedAtDesc(User reviewer);

  List<Review> findBySessionOrderByCreatedAtDesc(Session session);

  // 리뷰가 없으면 NULL 대신 0.0 반환
  @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.target = :target")
  Double calculateAverageRatingByTarget(@Param("target") User target);

  Long countByTarget(User target);
}