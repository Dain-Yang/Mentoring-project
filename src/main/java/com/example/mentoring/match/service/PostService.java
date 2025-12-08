package com.example.mentoring.match.service;

import com.example.mentoring.global.code.FieldCode;
import com.example.mentoring.global.code.LevelCode;
import com.example.mentoring.match.dto.CreatePostRequest;
import com.example.mentoring.match.dto.PostResponse;
import com.example.mentoring.match.dto.PostSummaryResponse;
import com.example.mentoring.member.repository.UserRepository;
import com.example.mentoring.member.entity.MentorProfile;
import com.example.mentoring.member.repository.MentorProfileRepository;
import com.example.mentoring.match.dto.UpdatePostRequest;
import com.example.mentoring.match.entity.Post;
import com.example.mentoring.match.entity.PostTag;
import com.example.mentoring.match.entity.Tag;
import com.example.mentoring.match.repository.PostRepository;
import com.example.mentoring.match.repository.TagRepository;
import com.example.mentoring.member.entity.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.mentoring.match.dto.PostSearchCondition;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

  private final PostRepository postRepository;
  private final TagRepository tagRepository;
  private final UserRepository userRepository;
  private final MentorProfileRepository mentorProfileRepository;

  @Transactional
  public PostResponse createPost(UUID userId, CreatePostRequest request) {
    // userId를 사용하여 User 객체 조회
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("작성자(User)를 찾을 수 없습니다."));

    if (user.getRole() != User.Role.MENTOR) {
      throw new IllegalStateException("멘토만 모집글을 작성할 수 있습니다.");
    }

    // 멘토 프로필 조회 및 레벨 검증 로직
    MentorProfile mentorProfile = mentorProfileRepository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("멘토 프로필을 찾을 수 없습니다."));

    LevelCode requestLevel = LevelCode.fromValue(request.getLevelCode());

    // 요청한 레벨이 멘토의 레벨보다 높으면 예외 발생 (Enum의 순서 비교)
    if (requestLevel.compareTo(mentorProfile.getLevelCode()) > 0) {
      throw new IllegalArgumentException("본인의 멘토 레벨보다 높은 레벨의 모집글은 작성할 수 없습니다.");
    }

    FieldCode fieldCode = FieldCode.fromValue(request.getFieldCode());

    // Post 객체 생성
    Post post = Post.builder()
        .user(user)
        .title(request.getTitle())
        .content(request.getContent())
        .fieldCode(fieldCode)
        .levelCode(requestLevel)
        .isRecruiting(true)
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
  public PostResponse updatePost(Integer postId, UUID userId, UpdatePostRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    if (!post.getUser().getId().equals(user.getId())) {
      throw new IllegalStateException("본인이 작성한 게시글만 수정할 수 있습니다.");
    }

    MentorProfile mentorProfile = mentorProfileRepository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("멘토 프로필을 찾을 수 없습니다."));

    LevelCode requestLevel = LevelCode.fromValue(request.getLevelCode());

    if (requestLevel.compareTo(mentorProfile.getLevelCode()) > 0) {
      throw new IllegalArgumentException("본인의 멘토 레벨보다 높은 레벨로 수정할 수 없습니다.");
    }

    FieldCode fieldCode = FieldCode.fromValue(request.getFieldCode());

    post.updatePost(request.getTitle(), request.getContent(), fieldCode, requestLevel);

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
  public void deletePost(Integer postId, UUID userId) {
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
    return postRepository.findByIsRecruitingTrueOrderByCreatedAtDesc(pageable)
        .map(PostResponse::from);
  }

  // 검색 및 필터링
  public Page<PostResponse> searchPosts(PostSearchCondition condition, Pageable pageable) {
    return postRepository.search(condition, pageable);
  }

  // 유저 글 목록 조회
  public List<PostSummaryResponse> getPostsByUserId(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("사용자(User)를 찾을 수 없습니다."));

    return postRepository.findByUserOrderByCreatedAtDesc(user).stream()
        .map(PostSummaryResponse::from)
        .collect(Collectors.toList());
  }

  // 모집 마감
  @Transactional
  public void closeRecruitment(Integer postId, UUID userId) {
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
  public void openRecruitment(Integer postId, UUID userId) {
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