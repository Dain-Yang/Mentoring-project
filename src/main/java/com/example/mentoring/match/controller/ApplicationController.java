package com.example.mentoring.match.controller;

import com.example.mentoring.auth.service.AuthService;
import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.match.dto.ApplicationResponse;
import com.example.mentoring.match.dto.ApplicationSummaryResponse;
import com.example.mentoring.match.dto.CreateApplicationRequest;
import com.example.mentoring.match.dto.UpdateApplicationRequest;
import com.example.mentoring.match.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

  private final ApplicationService applicationService;
  private final AuthService authService;

  // 신청서 제출 (멘티 전용)
  @PostMapping("/post/{postId}")
  public ResponseEntity<ApplicationResponse> createApplication(
      @PathVariable Integer postId,
      @Valid @RequestBody CreateApplicationRequest request) {

    CustomUserDetails currentUser = authService.getCurrentUser();

    ApplicationResponse response = applicationService.createApplication(postId, currentUser.getUserId(), request);
    return ResponseEntity.ok(response);
  }

  // 신청서 수정
  @PutMapping("/{applicationId}")
  public ResponseEntity<ApplicationResponse> updateApplication(
      @PathVariable Integer applicationId,
      @Valid @RequestBody UpdateApplicationRequest request) {

    CustomUserDetails currentUser = authService.getCurrentUser();

    ApplicationResponse response = applicationService.updateApplication(applicationId, currentUser.getUserId(), request);
    return ResponseEntity.ok(response);
  }

  // 신청서 삭제
  @DeleteMapping("/{applicationId}")
  public ResponseEntity<Void> deleteApplication(
      @PathVariable Integer applicationId) {

    CustomUserDetails currentUser = authService.getCurrentUser();

    applicationService.deleteApplication(applicationId, currentUser.getUserId());
    return ResponseEntity.noContent().build();
  }

  // 신청서 상세 조회
  @GetMapping("/{applicationId}")
  public ResponseEntity<ApplicationResponse> getApplication(
      @PathVariable Integer applicationId) {
    ApplicationResponse response = applicationService.getApplication(applicationId);
    return ResponseEntity.ok(response);
  }

  // 특정 게시글의 신청서 목록 조회 (멘토 전용)
  @GetMapping("/post/{postId}")
  public ResponseEntity<List<ApplicationResponse>> getApplicationsByPost(
      @PathVariable Integer postId) {

    CustomUserDetails currentUser = authService.getCurrentUser();

    List<ApplicationResponse> responses = applicationService.getApplicationsByPost(postId, currentUser.getUserId());
    return ResponseEntity.ok(responses);
  }

  // 내가 제출한 신청서 목록 조회 (멘티)
  @GetMapping("/my")
  public ResponseEntity<List<ApplicationSummaryResponse>> getMyApplications() {

    CustomUserDetails currentUser = authService.getCurrentUser();

    List<ApplicationSummaryResponse> responses = applicationService.getMyApplications(currentUser.getUserId());
    return ResponseEntity.ok(responses);
  }

  // 신청 승인 (멘토 전용)
  @PatchMapping("/mentor/{applicationId}/approve")
  public ResponseEntity<ApplicationResponse> approveApplication(
      @PathVariable Integer applicationId) {

    CustomUserDetails currentUser = authService.getCurrentUser();

    ApplicationResponse response = applicationService.approveApplication(applicationId, currentUser.getUserId());
    return ResponseEntity.ok(response);
  }

  // 신청 거절 (멘토 전용)
  @PatchMapping("/mentor/{applicationId}/reject")
  public ResponseEntity<ApplicationResponse> rejectApplication(
      @PathVariable Integer applicationId) {

    CustomUserDetails currentUser = authService.getCurrentUser();

    ApplicationResponse response = applicationService.rejectApplication(applicationId, currentUser.getUserId());
    return ResponseEntity.ok(response);
  }
}