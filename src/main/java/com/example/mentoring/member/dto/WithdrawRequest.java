package com.example.mentoring.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WithdrawRequest {
  // 회원 탈퇴 요청 dto

  private String password;
}