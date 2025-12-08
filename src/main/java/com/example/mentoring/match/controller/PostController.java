package com.example.mentoring.match.controller;

import com.example.mentoring.auth.service.AuthService;
import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.match.dto.CreatePostRequest;
import com.example.mentoring.match.dto.PostResponse;
import com.example.mentoring.match.dto.PostSearchCondition;
import com.example.mentoring.match.dto.PostSummaryResponse;
import com.example.mentoring.match.dto.UpdatePostRequest;
import com.example.mentoring.match.service.PostService;
import jakarta.validation.Valid; // 필수 Import
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
   //모집 게시글 (멘토 전용)

  private final PostService postService;
  private final AuthService authService;

  // 게시글 생성
  @PostMapping
  public ResponseEntity<PostResponse> createPost(
      @Valid @RequestBody CreatePostRequest request) {

    CustomUserDetails currentUser = authService.getCurrentUser();

    PostResponse response = postService.createPost(currentUser.getUserId(), request);
    return ResponseEntity.ok(response);
  }

  // 게시글 수정
  @PutMapping("/{postId}")
  public ResponseEntity<PostResponse> updatePost(
      @PathVariable Integer postId,
      @Valid @RequestBody UpdatePostRequest request) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    PostResponse response = postService.updatePost(postId, currentUser.getUserId(), request);
    return ResponseEntity.ok(response);
  }

  // 게시글 삭제
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(
      @PathVariable Integer postId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    postService.deletePost(postId, currentUser.getUserId());
    return ResponseEntity.noContent().build();
  }

  // 게시글 상세 조회 (모두 접근 가능)
  @GetMapping("/{postId}")
  public ResponseEntity<PostResponse> getPost(@PathVariable Integer postId) {
    PostResponse response = postService.getPost(postId);
    return ResponseEntity.ok(response);
  }

  // 모집 중인 게시글 목록 조회 (페이징 적용, 모두 접근 가능)
  @GetMapping
  public ResponseEntity<Page<PostResponse>> getAllPosts(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    Page<PostResponse> responses = postService.getAllPosts(pageable);
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/search")
  public ResponseEntity<Page<PostResponse>> searchPosts(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) List<Integer> tagIds,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    PostSearchCondition condition = new PostSearchCondition();
    condition.setKeyword(keyword);
    condition.setTagIds(tagIds);

    Page<PostResponse> result = postService.searchPosts(condition, pageable);
    return ResponseEntity.ok(result);
  }

  // 특정 유저(멘토)가 작성한 게시글 목록 조회
  @GetMapping("/list/{userId}")
  public ResponseEntity<List<PostSummaryResponse>> getPostsByUserId(@PathVariable UUID userId) {

    List<PostSummaryResponse> responses = postService.getPostsByUserId(userId);

    return ResponseEntity.ok(responses);
  }

  // 모집 마감
  @PatchMapping("/{postId}/close")
  public ResponseEntity<Void> closeRecruitment(
      @PathVariable Integer postId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    postService.closeRecruitment(postId, currentUser.getUserId());
    return ResponseEntity.ok().build();
  }

  // 모집 재개
  @PatchMapping("/{postId}/open")
  public ResponseEntity<Void> openRecruitment(
      @PathVariable Integer postId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    postService.openRecruitment(postId, currentUser.getUserId());
    return ResponseEntity.ok().build();
  }
}