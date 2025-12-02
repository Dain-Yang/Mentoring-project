package com.example.mentoring.match.repository;

import com.example.mentoring.match.entity.PostTag;
import com.example.mentoring.match.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Integer> {
  // 해당 태그를 사용 중인 게시글이 있는지 확인
  boolean existsByTag(Tag tag);
}