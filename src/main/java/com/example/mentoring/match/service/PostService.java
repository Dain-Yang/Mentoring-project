package com.example.mentoring.match.service;

import com.example.mentoring.global.code.FieldCode;
import com.example.mentoring.global.code.LevelCode;
import com.example.mentoring.match.dto.CreatePostRequest;
import com.example.mentoring.match.dto.PostResponse;
import com.example.mentoring.match.dto.PostSummaryResponse;
import com.example.mentoring.member.repository.UserRepository;
import com.example.mentoring.match.dto.UpdatePostRequest;
import com.example.mentoring.match.entity.Post;
import com.example.mentoring.match.entity.PostTag;
import com.example.mentoring.match.entity.Tag;
import com.example.mentoring.match.repository.PostRepository;
import com.example.mentoring.match.repository.TagRepository;
import com.example.mentoring.member.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final UserRepository userRepository;

  @Transactional
  public PostResponse createPost(Integer userId, CreatePostRequest request) {
    // userId를 사용하여 User 객체 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("작성자(User)를 찾을 수 없습니다."));

    if (user.getRole() != User.Role.MENTOR) {
      throw new IllegalStateException("멘토만 모집글을 작성할 수 있습니다.");
    }

    FieldCode fieldCode = FieldCode.fromValue(request.getFieldCode());
    LevelCode levelCode = LevelCode.fromValue(request.getLevelCode());

    // Post 객체 생성
    Post post = Post.builder()
        .user(user)
        .title(request.getTitle())
        .content(request.getContent())
        .fieldCode(fieldCode)
        .levelCode(levelCode)
        .status(true)
        .build();

    // 태그 저장
    if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
      List<Tag> tags = tagRepository.findAllById(request.getTagIds());

      tags.forEach(tag -> {
        PostTag postTag = PostTag.builder()
            .tag(tag)
            .post(post)
            .build();
        post.addPostTag(postTag);
      });
    }

    Post savedPost = postRepository.save(post);
    return PostResponse.from(savedPost);
  }

  // 게시글 수정
  @Transactional
  public PostResponse updatePost(Integer postId, Integer userId, UpdatePostRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if (!post.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("본인이 작성한 게시글만 수정할 수 있습니다.");
    }

    FieldCode fieldCode = FieldCode.fromValue(request.getFieldCode());
    LevelCode levelCode = LevelCode.fromValue(request.getLevelCode());

    post.updatePost(request.getTitle(), request.getContent(), fieldCode, levelCode);

    // 태그 업데이트 (기존 태그 삭제 후 재생성)
    post.clearPostTags();
    if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
      List<Tag> tags = tagRepository.findAllById(request.getTagIds());
      tags.forEach(tag -> {
        PostTag postTag = PostTag.builder()
            .tag(tag)
            .post(post) // 양방향 관계 유지를 위해 추가
            .build();
        post.addPostTag(postTag);
      });
    }

    return PostResponse.from(post);
  }

  // 게시글 삭제
  @Transactional
  public void deletePost(Integer postId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if (!post.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("본인이 작성한 게시글만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
  }

  public PostResponse getPost(Integer postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    return PostResponse.from(post);
  }

  // 페이징 적용: 메인 페이지 조회
  public Page<PostResponse> getAllPosts(Pageable pageable) {
    return postRepository.findByStatusTrueOrderByCreatedAtDesc(pageable)
        .map(PostResponse::from);
  }

  // 유저 글 목록 조회
  public List<PostSummaryResponse> getPostsByUserId(Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    return postRepository.findByUserOrderByCreatedAtDesc(user).stream()
        .map(PostSummaryResponse::from)
        .collect(Collectors.toList());
  }

  // 모집 마감
  @Transactional
  public void closeRecruitment(Integer postId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if (!post.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("본인이 작성한 게시글만 모집 마감할 수 있습니다.");
    }
    post.closeRecruitment();
  }

  // 모집 재개
  @Transactional
  public void openRecruitment(Integer postId, Integer userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if (!post.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("본인이 작성한 게시글만 모집 재개할 수 있습니다.");
    }
    post.openRecruitment();
  }

}