package com.example.mentoring.match.controller;

import com.example.mentoring.auth.service.AuthService;
import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.match.dto.SessionResponse;
import com.example.mentoring.match.dto.SessionSummaryResponse;
import com.example.mentoring.match.entity.Session;
import com.example.mentoring.match.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

  private final SessionService sessionService;
  private final AuthService authService;

  // 세션 상세 조회
  @GetMapping("/{sessionId}")
  public ResponseEntity<SessionResponse> getSession(
      @PathVariable Integer sessionId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    SessionResponse response = sessionService.getSession(sessionId, currentUser.getUserId());
    return ResponseEntity.ok(response);
  }

  // 내 세션 목록 조회
  @GetMapping("/my")
  public ResponseEntity<List<SessionSummaryResponse>> getMySessions(
      @RequestParam(required = false) Integer status) {

    CustomUserDetails currentUser = authService.getCurrentUser();

    if (status != null) {
      Session.SessionStatus sessionStatus = Session.SessionStatus.fromCode(status);
      List<SessionSummaryResponse> responses = sessionService.getMySessionsByStatus(currentUser.getUserId(), sessionStatus);
      return ResponseEntity.ok(responses);
    }

    List<SessionSummaryResponse> responses = sessionService.getMySessions(currentUser.getUserId());
    return ResponseEntity.ok(responses);
  }

  // 멘토링 종료 요청
  @PatchMapping("/{sessionId}/request-end")
  public ResponseEntity<SessionResponse> requestEndSession(
      @PathVariable Integer sessionId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    SessionResponse response = sessionService.requestEndSession(sessionId, currentUser.getUserId());
    return ResponseEntity.ok(response);
  }

  // 멘토링 종료 확인
  @PatchMapping("/{sessionId}/confirm-end")
  public ResponseEntity<SessionResponse> confirmEndSession(
      @PathVariable Integer sessionId) {

    CustomUserDetails currentUser = authService.getCurrentUser();
    SessionResponse response = sessionService.confirmEndSession(sessionId, currentUser.getUserId());
    return ResponseEntity.ok(response);
  }
}