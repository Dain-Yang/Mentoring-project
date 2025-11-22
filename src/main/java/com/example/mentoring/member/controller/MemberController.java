package com.example.mentoring.member.controller;

import com.example.mentoring.auth.service.AuthService;
import com.example.mentoring.auth.service.CustomUserDetails;
import com.example.mentoring.member.dto.SignUpRequest;
import com.example.mentoring.member.dto.UpdateUserRequest;
import com.example.mentoring.member.dto.UserResponse;
import com.example.mentoring.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;
  private final AuthService authService;

  // 회원가입
  @PostMapping("/signup")
  public ResponseEntity<UserResponse> signUp(@Valid @RequestBody SignUpRequest request) {
    UserResponse response = memberService.signUp(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 마이페이지 - 내 기본 정보 조회 (공통)
  @GetMapping("/mypage")
  public ResponseEntity<UserResponse> getMyInfo() {
    CustomUserDetails currentUser = authService.getCurrentUser();
    UserResponse response = memberService.getUserById(currentUser.getUserId());
    return ResponseEntity.ok(response);
  }

  // 마이 페이지 - 내 기본 정보 수정 (닉네임, 비밀번호 등)
  @PutMapping("/mypage")
  public ResponseEntity<UserResponse> updateMyInfo(@Valid @RequestBody UpdateUserRequest request) {
    CustomUserDetails currentUser = authService.getCurrentUser();
    UserResponse response = memberService.updateUser(currentUser.getUserId(), request);
    return ResponseEntity.ok(response);
  }
}