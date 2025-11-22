package com.example.mentoring.member.controller;

import com.example.mentoring.auth.service.AuthService;
import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.member.dto.CreateMentorProfileRequest;
import com.example.mentoring.member.dto.MentorProfileResponse;
import com.example.mentoring.member.dto.UpdateMentorProfileRequest;
import com.example.mentoring.member.service.MentorService;
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
@RequestMapping("/mentor")
@RequiredArgsConstructor
public class MentorController {

  private final MentorService mentorService;
  private final AuthService authService;

  // 멘토 프로필 생성
  @PostMapping("/profile")
  public ResponseEntity<MentorProfileResponse> createMentorProfile(
      @Valid @RequestBody CreateMentorProfileRequest request) {
    CustomUserDetails currentUser = authService.getCurrentUser();
    MentorProfileResponse response = mentorService.createMentorProfile(
        currentUser.getUserId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 멘토 프로필 조회
  @GetMapping("/profile")
  public ResponseEntity<MentorProfileResponse> getMyMentorProfile() {
    CustomUserDetails currentUser = authService.getCurrentUser();
    MentorProfileResponse response = mentorService.getMentorProfile(currentUser.getUserId());
    return ResponseEntity.ok(response);
  }

  // 멘토 프로필 수정
  @PutMapping("/profile")
  public ResponseEntity<MentorProfileResponse> updateMentorProfile(
      @Valid @RequestBody UpdateMentorProfileRequest request) {
    CustomUserDetails currentUser = authService.getCurrentUser();
    MentorProfileResponse response = mentorService.updateMentorProfile(
        currentUser.getUserId(), request);
    return ResponseEntity.ok(response);
  }
}