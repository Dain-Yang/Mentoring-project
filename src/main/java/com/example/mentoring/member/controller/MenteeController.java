package com.example.mentoring.member.controller;

import com.example.mentoring.auth.service.AuthService;
import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.member.dto.CreateMenteeProfileRequest;
import com.example.mentoring.member.dto.MenteeProfileResponse;
import com.example.mentoring.member.dto.UpdateMenteeProfileRequest;
import com.example.mentoring.member.service.MenteeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mentee")
@RequiredArgsConstructor
public class MenteeController {

  private final MenteeService menteeService;
  private final AuthService authService;

  // 멘티 프로필 생성
  @PostMapping("/profile")
  public ResponseEntity<MenteeProfileResponse> createMenteeProfile(
      @Valid @RequestBody CreateMenteeProfileRequest request) {
    CustomUserDetails currentUser = authService.getCurrentUser();
    MenteeProfileResponse response = menteeService.createMenteeProfile(
        currentUser.getUserId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 멘티 프로필 조회
  @GetMapping("/profile")
  public ResponseEntity<MenteeProfileResponse> getMyMenteeProfile() {
    CustomUserDetails currentUser = authService.getCurrentUser();
    MenteeProfileResponse response = menteeService.getMenteeProfile(currentUser.getUserId());
    return ResponseEntity.ok(response);
  }

  // 멘티 프로필 수정
  @PutMapping("/profile")
  public ResponseEntity<MenteeProfileResponse> updateMenteeProfile(
      @Valid @RequestBody UpdateMenteeProfileRequest request) {
    CustomUserDetails currentUser = authService.getCurrentUser();
    MenteeProfileResponse response = menteeService.updateMenteeProfile(
        currentUser.getUserId(), request);
    return ResponseEntity.ok(response);
  }
}