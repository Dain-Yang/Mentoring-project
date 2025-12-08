package com.example.mentoring.match.repository;

import com.example.mentoring.match.dto.PostResponse;
import com.example.mentoring.match.dto.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
  Page<PostResponse> search(PostSearchCondition condition, Pageable pageable);
}