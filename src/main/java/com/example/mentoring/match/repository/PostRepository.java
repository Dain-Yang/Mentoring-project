package com.example.mentoring.match.repository;

import com.example.mentoring.match.entity.Post;
import com.example.mentoring.member.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {

  // 마이페이지: 내가 쓴 글 조회
  List<Post> findByUserOrderByCreatedAtDesc(User user);

  // 메인 게시판: 모집 중인 글(태그 매핑 + 실제 태그) 조회 + 작성자 정보 즉시 로딩
  @EntityGraph(attributePaths = {"user", "postTags", "postTags.tag"})
  Page<Post> findByIsRecruitingTrueOrderByCreatedAtDesc(Pageable pageable);

  // 상세 조회: 작성자와 태그들을 모두 한 번에 조회
  @EntityGraph(attributePaths = {"user", "postTags", "postTags.tag"})
  Optional<Post> findById(Integer id);
}