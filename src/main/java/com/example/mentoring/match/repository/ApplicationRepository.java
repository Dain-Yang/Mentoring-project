package com.example.mentoring.match.repository;

import com.example.mentoring.match.entity.Application;
import com.example.mentoring.match.entity.Post;
import com.example.mentoring.member.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

  // 멘티 마이페이지: 내 신청서 조회 (어떤 멘토의 모집글에 신청했는지)
  @EntityGraph(attributePaths = {"post", "post.user"})
  List<Application> findByUserOrderByAppliedAtDesc(User user);

  // 멘토의 게시글 관리: 특정 글에 달린 신청서 목록
  @EntityGraph(attributePaths = {"user"})
  List<Application> findByPostOrderByAppliedAtDesc(Post post);

  // 멘토 마이페이지: 나(멘토)에게 온 모든 신청서
  @EntityGraph(attributePaths = {"user", "post"})
  List<Application> findByPost_UserOrderByAppliedAtDesc(User mentor);

  // 중복 신청 확인
  boolean existsByPostAndUser(Post post, User user);
}